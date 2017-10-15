# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure('2') do |config|
  config.vm.box = 'centos/7'

  config.vm.network 'forwarded_port', guest: 22, host: 2222, disabled: true
  config.vm.network 'private_network', ip: '192.168.45.10'
  config.vm.network 'forwarded_port', guest: 22, host: 2245, id: 'ssh'

  config.vm.synced_folder '.', '/vagrant', type: 'rsync'

  config.vbguest.auto_update = false
  config.vm.provider 'virtualbox' do |vb|
    vb.gui = false
    vb.cpus = 4
    vb.memory = 8192
    vb.customize ['modifyvm', :id, '--natdnshostresolver1', 'on']
  end
  config.ssh.forward_agent = true
  config.ssh.username = 'root'
  config.ssh.password = 'hadoop'
end
