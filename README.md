# JGate
QQ交流群：726354579

JGate是一个致力于提升开发效率的通用网关框架，代码简洁易懂，开发者不需要修改JGate的代码，只需集中于自身业务端逻辑。

一、主要特色   
1、开发语言为Java，底层基于高性能的netty，目前支持tcp协议，即将支持udp/http/websocket   
2、基于redis的订阅发布机制，使您无需修改JGate自身代码，只需要订阅redis的channel即可   
3、支持监听多个端口，并根据配置的路由规则发布到对应的channel   
4、后端业务服务器支持多语言，提供详细的demo，包括unity客户端、C#客户端、C#业务服务端   
5、使用场景:棋牌、独游以及其他类型的服务端，如果有不适合的场景，请提出建议，开发者会添加支持!   

二、配置文件说明   
1、enableNettyLogging：如果为true，会把JGate接收和收到的客户端消息以十六进制的格式打印到控制台和日志文件中，提升客户端和服务端的调试效率   
2、redis.host和redis.port为redis部署的机器和监听端口，redis本身支持windows/linux多平台部署   
3、subscriber.channel：JGate订阅的channel，后端业务服务器通过该channel向JGate发送消息   
4、publisher.channel:JGate监听端口到后端channel的路由规则，多个端口直接用逗号分隔   
	<publisher>   
        <!--port->channel,表示将从port端口接收的包转发到channel通道中-->   
        <channel>18800->ddz,28800->ddz</channel>>   
    </publisher>>   

三、部署方式   
1、使用Idea或者Eclipse的打包，会生成jgate-1.0-SNAPSHOT-jar-with-dependencies.jar文件，然后执行   
	java -jar jgate-1.0-SNAPSHOT-jar-with-dependencies.jar   
	
2、就这么简单   
	
四、TODO   
1、udp/http/websocket的支持   
2、web管理平台，方便管理配置文件以及一键部署   
3、等等   

