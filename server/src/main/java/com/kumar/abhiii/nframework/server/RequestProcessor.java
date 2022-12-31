		//eg372.java

package com.kumar.abhiii.nframework.server;
import java.net.*;
import java.io.*;
import java.nio.charset.*;
import java.lang.reflect.*;
import com.kumar.abhiii.nframework.common.*;
class RequestProcessor extends Thread
{
private NFrameworkServer server;
private Socket socket;
RequestProcessor(NFrameworkServer server,Socket socket)
{
this.server=server;
this.socket=socket;
start();
}
public void run()
{
try
{
//copy from networking/example3/server3
//now read header
InputStream is=socket.getInputStream();
OutputStream os=socket.getOutputStream();
int bytesToReceive=1024;
byte tmp[]=new byte[1024];
byte header[]=new byte[1024];
int bytesReadCount;
int k,j,i;
i=0;
j=0;
while(j<bytesToReceive)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
header[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
int requestLength=0;
i=1;
j=1023;
while(j>=0)
{
requestLength=requestLength+(header[j]*i);
i=i*10;
j--;
}
// now send acknoladgement to client
byte ack[]=new byte[1];
ack[0]=1;
os.write(ack,0,1);
os.flush();

//Read response 
byte request[]=new byte[requestLength];
bytesToReceive=requestLength;
i=0;
j=0;
while(j<bytesToReceive)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
request[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
String requestJSONString=new String(request,StandardCharsets.UTF_8);
Request requestObject=JSONUtil.fromJSON(requestJSONString,Request.class);

//the request object contains service path and argument
//we want the refrence of the TCPService that contains the Class ref and Mthod ref

String servicePath=requestObject.getServicePath();
TCPService tcpService=this.server.getTCPService(servicePath);
Response responseObject=new Response();
if(tcpService==null)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException(new RuntimeException("Invalid path :"+servicePath));
}
else
{
Class c=tcpService.c;
Method method=tcpService.method;
try
{
Object serviceObject=c.newInstance();
Object result=method.invoke(serviceObject,requestObject.getArguments());
responseObject.setSuccess(true);
responseObject.setResult(result);
responseObject.setException(null);
}catch(InstantiationException instantiationException)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException(new RuntimeException("Unable to create object of service class associated with the path : "+servicePath));
}
catch(IllegalAccessException illegalAccessException)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException(new RuntimeException("Unable to create object of service class associated with the path : "+servicePath));
}
catch(InvocationTargetException invocationTargetException)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
Throwable t=invocationTargetException.getCause();
responseObject.setException(t);

}
}

String responseJSONString=JSONUtil.toJSON(responseObject);
byte objectBytes[]=responseJSONString.getBytes(StandardCharsets.UTF_8);
//now we have to know the length of objectByte
int responseLength=objectBytes.length;
// creating header
int x;
i=1023;
x=responseLength;
header=new byte[1024];
while(x>0)
{
header[i]=(byte)(x%10);
x=x/10;
i--;
}
os.write(header,0,1024); 	//from which index, how many
os.flush();
System.out.println("Response Header sent : "+responseLength);
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
System.out.println("Acknowledgement received");
//now start sending data
int bytesToSend=responseLength;
int chunkSize=1024;
j=0;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize=bytesToSend-j;
os.write(objectBytes,j,chunkSize);
os.flush();
j=j+chunkSize;
}
System.out.println("Response sent");
//read ack
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
System.out.println("Ackowladgement received ");
socket.close();


}catch(IOException e)
{
System.out.println(e);
}
}
}