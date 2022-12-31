package com.thinking.machines.nframework.common;
public class Response implements java.io.Serializable
{
private boolean success;
private Object result;
private Object exception;
public void setSuccess(boolean success)
{
this.success=success;
}
public boolean getSuccess()
{
return this.success;
}
public void setResult(Object result)
{
this.result=result;
}
public Object getResult()
{
return this.result;
}
public void setException(Object exception)
{
this.exception=exception;
}
public Object getException()
{
return this.exception;
}
public boolean hasException()
{
return this.success==false;
}

}