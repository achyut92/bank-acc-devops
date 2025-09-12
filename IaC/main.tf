provider "aws" {
  region = "ap-southeast-1"
}

# Random password for Aurora DB
resource "random_password" "db" {
  length  = 16
  special = true
}

data "aws_iam_policy_document" "eks_fargate_sa" {
  statement {
    effect  = "Allow"
    principals {
      type        = "Federated"
      identifiers = [module.eks.oidc_provider_arn]
    }
    actions = ["sts:AssumeRoleWithWebIdentity"]
  }
}

# VPC
module "vpc" {
  source = "terraform-aws-modules/vpc/aws"
  name = "bank-app-vpc"
  cidr = "10.0.0.0/16"
  azs  = ["ap-southeast-1a","ap-southeast-1b"]
  public_subnets  = ["10.0.1.0/24","10.0.2.0/24"]
  private_subnets = ["10.0.101.0/24","10.0.102.0/24"]
}

# Aurora MySQL
module "aurora" {
  source                = "terraform-aws-modules/rds-aurora/aws"
  name                  = "bank-db"
  engine                = "aurora-mysql"
  engine_version        = "8.0.mysql_aurora.3.04.0"
  master_username       = "admin"
  master_password       = random_password.db.result
  vpc_id                = module.vpc.vpc_id
  db_subnet_group_name  = module.vpc.private_subnets
  enable_http_endpoint  = true
}

# EKS cluster
module "eks" {
  source          = "terraform-aws-modules/eks/aws"
  kubernetes_version = "1.33"
  vpc_id          = module.vpc.vpc_id
  subnet_ids      = module.vpc.private_subnets
  eks_managed_node_groups = {
    fargate = {
      desired_capacity = 2
      instance_type    = "t3.micro"
    }
  }
  fargate_profiles = {
    bank_app_profile = {
      selectors = [{ namespace = "bank-app" }]
    }
  }
}

resource "aws_iam_role" "eks_fargate_secrets" {
  name = "bank-app-fargate-role"

  assume_role_policy = data.aws_iam_policy_document.eks_fargate_sa.json
}

# IAM role attachment for Fargate pods to access Secrets Manager
resource "aws_iam_role_policy_attachment" "eks_secrets" {
  role       = aws_iam_role.eks_fargate_secrets.name
  policy_arn = "arn:aws:iam::aws:policy/SecretsManagerReadWrite"
}

# ALB
resource "aws_lb" "bank_app_alb" {
  name               = "bank-app-alb"
  internal           = false
  load_balancer_type = "application"
  subnets            = module.vpc.public_subnets
}

# Target group for ALB
resource "aws_lb_target_group" "bank_app_tg" {
  name     = "bank-app-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = module.vpc.vpc_id
}

# ALB Listener
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.bank_app_alb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.bank_app_tg.arn
  }
}

# API Gateway
resource "aws_apigatewayv2_api" "bank_api" {
  name          = "bank-api"
  protocol_type = "HTTP"
}

# API Gateway integration with ALB
resource "aws_apigatewayv2_integration" "alb_integration" {
  api_id           = aws_apigatewayv2_api.bank_api.id
  integration_type = "HTTP_PROXY"
  integration_uri  = aws_lb.bank_app_alb.dns_name
}

resource "aws_ecr_repository" "bank_app" {
  name = "bank-app"
}

# Secrets Manager (for password)
resource "aws_secretsmanager_secret" "bank_db_secret" {
  name = "bank-db-secret"
}

resource "aws_secretsmanager_secret_version" "bank_db_secret_version" {
  secret_id     = aws_secretsmanager_secret.bank_db_secret.id
  secret_string = jsonencode({
    password = random_password.db.result
  })
}

# IAM role for external secret
resource "aws_iam_role" "external_secrets_role" {
  name               = "bank-app-external-secrets-role"
  assume_role_policy = data.aws_iam_policy_document.eks_fargate_sa.json
}

resource "aws_iam_role_policy_attachment" "external_secrets_policy" {
  role       = aws_iam_role.external_secrets_role.name
  policy_arn = "arn:aws:iam::aws:policy/SecretsManagerReadWrite"
}

output "api_endpoint" {
  value = aws_apigatewayv2_api.bank_api.api_endpoint
}

output "aurora_cluster_endpoint" {
  description = "Aurora cluster writer endpoint"
  value       = module.aurora.cluster_endpoint
}

output "external_secrets_role_arn" {
  description = "IAM role arn for SecretStore"
  value       = aws_iam_role.external_secrets_role.arn
}