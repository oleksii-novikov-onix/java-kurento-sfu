Vagrant.configure("2") do |config|
    config.vm.box = "hbsmith/awslinux"
    config.vm.synced_folder "./", "/src"
    config.vm.provider "virtualbox" do |v|
      v.memory = 1024
      v.cpus = 2
    end
    config.vm.network "forwarded_port", guest: 8888, host: 8888
    config.vm.provision "shell", path: 'vagrant-provision.sh'
end
