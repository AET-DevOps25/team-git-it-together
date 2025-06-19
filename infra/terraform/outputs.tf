output "instance_id" {
  description = "ID of the EC2 instance"
  value = aws_instance.ubuntu.id
}

output "public_ip" {
  description = "Public IP of the EC2 instance"
  value = data.aws_eip.used.public_ip
}