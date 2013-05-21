package com.untamedears.DynCap;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class QueueItem 
{
	private Date lastAttempt;
	private String name;
	
	public QueueItem(String playerName)
	{
		lastAttempt = new Date();
		name = playerName;
	}
	public Date getLastAttempt()
	{
		return lastAttempt;
	}
	public int getSecondsSinceLastAttempt()
	{
		Date now = new Date();
		return (int) TimeUnit.MILLISECONDS.toSeconds(now.getTime() - lastAttempt.getTime());
	}
	public void updateDate()
	{
		lastAttempt = new Date();
	}
	public String getName()
	{
		return name;
	}
}
