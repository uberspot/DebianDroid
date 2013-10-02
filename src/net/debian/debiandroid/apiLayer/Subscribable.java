package net.debian.debiandroid.apiLayer;

import java.util.Set;

public interface Subscribable {

	public boolean isSubscribedTo(String subcriptionID);

	public boolean removeSubscriptionTo(String subcriptionID);

	public boolean addSubscriptionTo(String subcriptionID);
	
	public Set<String> getSubscriptions();
}
