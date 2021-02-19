#!/bin/bash

sudo yum update -y

sudo yum install -y docker
sudo service docker start
sudo chkconfig docker on
sudo curl -L "https://github.com/docker/compose/releases/download/1.25.4/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
cd /vagrant
sudo docker-compose up -d
