# Terraform AWS Infra for SkillForge

## Usage

1. `terraform init`
    - Initialize the Terraform configuration.
    - Ensure you have the AWS CLI configured in your environment.
2. `terraform plan`
    - Review the planned actions before applying.
    - This will show you what resources will be created or modified.
3. `terraform apply`
    - Apply the changes required to reach the desired state of the configuration.
    - This will create or modify the resources in your AWS account.

## Variables

- `aws_region`: AWS region to deploy resources (default: `us-east-1`)
- `instance_type`: Type of EC2 instance to launch (default: `t2.small`)
- `key_name`: Name of the SSH key pair to use for the instance (default: `vockey`)
- `ami_id`: AMI ID for Ubuntu (default provided)
- `eip_allocation_id`: Allocation ID of your Elastic IP

## Outputs

- Instance ID: The ID of the created EC2 instance.
- Public IP: The public IP address assigned to the EC2 instance.
- Elastic IP: The Elastic IP address associated with the EC2 instance.
