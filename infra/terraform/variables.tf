variable "aws_region" {
  description = "AWS region to deploy into"
  default     = "us-east-1"
}

variable "instance_type" {
  description = "Type of EC2 instance"
  default     = "t2.small"
}

variable "key_name" {
  description = "Name of the existing EC2 Key Pair"
  type        = string
}

variable "ami_id" {
  description = "AMI ID for Ubuntu"
  default     = "ami-020cba7c55df1f615" # Ubuntu 22.04 LTS in us-east-1
}

variable "eip_allocation_id" {
  description = "The allocation ID of an existing Elastic IP"
  type        = string
}
