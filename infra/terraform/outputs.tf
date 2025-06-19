output "instance_id" {
  value = aws_instance.ubuntu.id
}

output "public_ip" {
  value = data.aws_eip.used.public_ip
}