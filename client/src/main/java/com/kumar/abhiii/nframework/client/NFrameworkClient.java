package com.kumar.abhiii.nframework.client;
import com.kumar.abhiii.nframework.common.*;
import com.kumar.abhiii.nframework.common.exceptions.*;
import java.nio.charset.*;
import java.net.*;
import java.io.*;
public class NFrameworkClient
{
public Object execute(String servicePath,Object ...arguments) throws Throwable
{
try
{
//copy code from example3/client3

Request request=new Request();
request.setServicePath(servicePath);
request.setArguments(arguments);
String requestJSONString=JSONUtil.toJSON(request);

byte objectBytes[];
objectBytes=requestJSONString.getBytes(StandardCharsets.UTF_8);
//now we have to know the length of objectByte
int requestLength=objectBytes.length;
// creating header
byte header[]=new byte[1024];
int x;
int i=1023;
x=requestLength;
while(x>0)
{
header[i]=(byte)(x%10);
x=x/10;
i--;
}
//now connect into socket and stablish the connection
Socket socket=new Socket("localhost",5500);
OutputStream os=socket.getOutputStream();
os.write(header,0,1024); 	//from which index, how many
os.flush();
// ham samne wale se acknolagement mangenge, jab tak udahr se acknolagement nahi aayega tab tak ham aage nai badhenge, agar acknolagement aa gaya to ham data bhejna start karenge.
InputStream is=socket.getInputStream();
byte ack[]=new byte[1];
int bytesReadCount;
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
//now start sending data
int bytesToSend=requestLength;
int chunkSize=1024;
int j=0;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize=bytesToSend-j;
os.write(objectBytes,j,chunkSize);
os.flush();
j=j+chunkSize;
}
//now read header
int bytesToReceive=1024;
byte tmp[]=new byte[1024];
int k;
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
int responseLength=0;
i=1;
j=1023;
while(j>=0)
{
responseLength=responseLength+(header[j]*i);
i=i*10;
j--;
}
// now sent acknowladgement to server
ack[0]=1;
os.write(ack,0,1);
os.flush();
//Read response : we know that how much data is come, so we create the array size of responseLength.
byte response[]=new byte[responseLength];
bytesToReceive=responseLength;
i=0;
j=0;
while(j<bytesToReceive)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
response[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
ack[0]=1;
os.write(ack);
os.flush();
socket.close();

String responseJSONString=new String(response,StandardCharsets.UTF_8);
Response responseObject=JSONUtil.fromJSON(responseJSONString,Response.class);

if(responseObject.getSuccess())
{
return responseObject.getResult();
}
else
{
throw responseObject.getException();
}
}catch(Exception e)
{
System.out.println(e);
}
return null;
}
}