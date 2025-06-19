# Ansible Playbook for Docker Setup
This playbook installs and configures Docker on the target servers.

## Usage
1. Ensure you have Ansible installed on your control machine.
2. Update the `inventory` file with your target server details and ssh key.

   Example `inventory.ini` file:
   ```ini
   [git_it_together]
   server_ip ansible_user=ubuntu ansible_ssh_private_key_file=/path/to/your/private/key.pem
   ```

3. Review the changes to apply by running:
   ```bash
   ansible-playbook -i inventory playbook.yml --check
   ```
4. To apply the changes, run:
   ```bash
   ansible-playbook -i inventory playbook.yml
   ```
