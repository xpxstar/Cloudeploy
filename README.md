# Wellcome to Cloudeploy
## How this documentation is organized
> Introductions of Cloudeploy.

> Requirements and installation

> Demo Case

> License

------------

## What is Cloudeploy？
Cloudeploy is an online platform for deploying and configuring cloud based applications.User can easily achieve the following functions without high level specific domain knowledge.

> * Continuous deployment and configuration for cloud based applications(such as Tomcat,Nginx,MySQL etc.) .
> * Management of cloud based components on topology relations and consistency of parameters.
> * Health monitor and report for middleware instances.
> * Dynamic scale on numbers of middleware instances.
> * Fault Self-tolerance for instances.

## How to install Cloudeploy?
Required Files :
```
Cloudeploy.zip
cloudeploy.sql
ConsulAgent.zip
Consul-template
mydb
```
Overview:The Frame of this platform consists of two part: agent cluster and servers, as the figure1 shows.


### 1.Install agent cluster
Agent cluster consists of at least three computers(agent 1-3 in figure1), which are hosts of cloud based applications. For every agent, the environment is built as follows.

```
OS：Liunx-x64
Java  (version >= 7.0)
docker(version >= 1.9)[Download and Install](https://docs.docker.com/engine/installation/linux/)
consul(version >= 0.6)[Download and Install](https://www.consul.io/intro/getting-started/install.html)
consul-UI [Download and unzip to /opt/ui](https://releases.hashicorp.com/consul/0.6.4/consul_0.6.4_web_ui.zip)
```

#### (1)Docker
>start docker with listening socket of tcp:

```shell
docker daemon -H tcp://0.0.0.0:2375 &
```
>download images that needed:

```shell
docker pull xpxstar/mysql
docker pull xpxstar/tomcat
docker pull docker.io/nginx
```
#### (2)Consul
>start consul on agent1、agent2 with model of server and add itself to consul cluster.

```shell
consul agent -server -data-dir /opt/data/ -ui-dir /opt/ui -bootstrap-expect 2 -node=node1 -dc=dc1 -bind=<agent-ip> -client=0.0.0.0 &
consul join <agent1-ip>
```
>start consul on agent3 and other agent with model of agent,and add itself to consul cluster

```
consul agent -data-dir /opt/data/ -ui-dir /opt/ui -node=node3 -dc=dc1 -bind=<agent-ip> -client=0.0.0.0 &
consul join <agent1-ip>
```

#### (3)consul-template
>make several directory:

```
mkdir /opt/agent/{consul-template,bolt,template,packet,volume}
```
>start consul-template:

```
consul-template consul-template -config /etc/consul-template --consul 0.0.0.0:8500 -retry=1s &
```
#### (4)consulAgent
>unzip file 'consulAgent.zip' to /opt/agent, and start consulAgent.jar with the following command:

```
java -jar consulAgent-0.0.1-SNAPSHOT.jar
```

###### notice: you should open some ports through firewall of agent.：
```
2375,3306,8300,8301,8301,8400,8500,8600,8380
```
### 2.Install Servers
There are Web server,Database server,File server.The OS of server hosts can be linux or windows.And You can install them in the same host as you like.
>Environment of Host A:

```
MySQL(version >= 5.1) [Download and Install](http://dev.mysql.com/downloads/mysql/)
```
>start mysql and create database named cloudeploy.Import cloudeploy.sql into database to build data structures.
Environment of Host B and C
Here we can put file servr and web server into the same Host, named Host B
>Environment of Host B

```
JAVA (version >= 7.0)
Tomcat (version>=7.0.30) [Download and Install] (https://tomcat.apache.org/download-70.cgi)
```
>unzip cloudeploy.zip to directory of "$TOMCAT_PATH/webapps",edit configure files：

Cloudeploy/cloudeploy_app/src/main/resources/application.properties:
```
jdbc.url=<HostA-ip>
consul_host=<agent1-ip>
```
Cloudeploy/config/src/main/resources/configs.properties:
```
deploy.server.host=<agent1-ip>
server.root=http://<Host B-ip>:8080/cloudapp
```
>start tomcat

```
sh $TOMCAT_PATH/bin/startup.sh
```
>visit url of "http://HostB-ip:8080/cloudapp/init" to initialize demo data and finish installing
## User Demo Guide
To see in Demo.md
## Develpment based on Cloudeploy

Server project of Cloudeploy_app is develped by java with maven,so just import the project into Eclipse in maven environment.

Client Agent project of ConsulAgent is a normal java project.

FAQ:
Support By Pershing [xupeixing14@otcaix.iscas.ac.cn]
with License under GNU showing in  LICENSE.
