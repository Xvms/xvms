# xvms
 A Java from the server as a Linux transcoding open source software, from upload to transcoding to storage distribution, and finally released (PC / Mobile) to the CMS system

# Point
Based on rights management , Video files for streaming media, compression ratio, high definition, Streaming media play is not cardton , API docking CMS

# Configuration
CentOS 6.X + Nginx + red5-1.0.0 + ffmpeg + JDK1.6+ +.net(Client) + php5(web)

Nginx listen port 1395

# nginx Reverse proxy settings
server 
	{
    	listen          8181;
    	server_name xxxx.com;
        limit_rate_after 3m;
        limit_rate 160k;

    	location / {
        	proxy_pass              http://192.168.1.24:5080/live/rtmpt/;
        	proxy_redirect          off;
        	proxy_set_header        X-Real-IP       $remote_addr;
        	proxy_set_header        X-Forwarded-For $proxy_add_x_forwarded_for;
        	}
	}
 
 
 # run
 java -jar coderServer.jar


# Thanks to the following organizations for their contributions to open source
ffmpeg：https://ffmpeg.org/

mina2： http://mina.apache.org/

red5：https://www.red5.co.uk/

nginx：http://nginx.org

php：http://www.php.net

mysql: https://www.mysql.com/

log4j: http://logging.apache.org/log4j/

HttpComponents: http://hc.apache.org/downloads.cgi

# Copyright

Xvms follow the GUN open source protocol release and provide free use.

The third party source code and the copyright information of the binary file included in this project are marked separately.

Copyright © 2017-2018 by appqy @ fy (http://www.appqy.com)

All rights reserved.
