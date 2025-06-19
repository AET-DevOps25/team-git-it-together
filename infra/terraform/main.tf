terraform {
  required_version = ">= 1.12"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# The used Elastic IP that will be associated with the EC2 instance
data "aws_eip" "used" {
  id = var.eip_allocation_id
}

# Configure the AWS provider and region
provider "aws" {
  region = var.aws_region
}

# VPC: Creates an isolated virtual network in AWS for your resources
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16" # Defines the IP address range for the VPC (here, 10.0.0.0 – 10.0.255.255).
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = { Name = "git-it-together-main-vpc" }
}

# Subnet: Public subnet inside the VPC for launching EC2 instances
resource "aws_subnet" "main" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.1.0/24" # This subnet has IPs from 10.0.1.0 to 10.0.1.255.
  map_public_ip_on_launch = true

  tags = { Name = "git-it-together-main-subnet" }
}

# Internet Gateway: Allows resources in the VPC to access the Internet
resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id

  tags = { Name = "git-it-together-main-gw" }
}

# Route Table: Routes traffic from the subnet to the Internet Gateway (for outbound internet access)
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  # any outbound traffic (to any IP, 0.0.0.0/0) goes through the Internet Gateway.
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gw.id
  }

  tags = { Name = "git-it-together-main-rt" }
}

# Attaches the route table with the subnet so traffic uses the Internet Gateway
resource "aws_route_table_association" "public_a" {
  subnet_id      = aws_subnet.main.id
  route_table_id = aws_route_table.public.id
}

# Security Group: Allows SSH access from any IP
resource "aws_security_group" "ssh" {
  name        = "allow-ssh"
  description = "Allow SSH inbound traffic"
  vpc_id      = aws_vpc.main.id

  # Allow inbound SSH from anywhere (port 22)
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  # Allow all outbound traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "git-it-together-ssh-sg"
  }
}

# EC2 Instance: Launches Ubuntu server in public subnet and attaches SSH security group
resource "aws_instance" "ubuntu" {
  ami                         = var.ami_id
  instance_type               = var.instance_type
  key_name                    = var.key_name
  subnet_id                   = aws_subnet.main.id          # Launch in the public subnet
  vpc_security_group_ids      = [aws_security_group.ssh.id]
  associate_public_ip_address = false                       # Because we are using an Elastic IP and we will associate it later

  tags = {
    Name = "skill-forge-prod"
  }

}

# Associate existing Elastic IP to new instance
resource "aws_eip_association" "ubuntu_ip" {
  instance_id   = aws_instance.ubuntu.id
  allocation_id = var.eip_allocation_id
}
