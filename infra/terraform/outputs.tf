output "instance_id" {
  value = aws_instance.ubuntu.id
}

output "public_ip" {
  value = aws_instance.ubuntu.public_ip
}

output "eip_address" {
  value = var.eip_allocation_id
}
