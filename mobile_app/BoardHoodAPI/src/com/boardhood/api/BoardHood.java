package com.boardhood.api;

import com.boardhood.api.model.Conversation;
import com.boardhood.api.model.Coordinates;
import com.boardhood.api.model.Interest;
import com.boardhood.api.model.User;
import com.boardhood.api.util.BHList;
import com.boardhood.api.util.CacheProvider;

import java.util.Date;

public interface BoardHood {
	public static final String ORDER_POPULAR = "popular";
	public static final String ORDER_RECENT = "recent";
	public static final String ORDER_DISTANCE = "distance";
	
	/*
	 * Utils
	 */
	public void setCacheProvider(CacheProvider<String, String> cache);
	public CacheProvider<String, String> getCacheProvider();
	
	/*
     * Authentication
     */
	public abstract void setApiKey(String apiKey);
    public abstract void setCredentials(String username, String password);
    public abstract boolean authenticateUserCredentials() throws Exception;
    
    /*
     * Interest
     */
    public abstract BHList<Interest> searchInterests(String name) throws Exception;
    public abstract BHList<Interest> listInterests(String order, int perPage, int page) throws Exception;
    public abstract BHList<Interest> listInterests(String order, int radius, Coordinates coord, int perPage, int page) throws Exception;
    public abstract Interest createInterest(Interest i) throws Exception;
    public abstract Interest findInterest(String id) throws Exception;
    public abstract BHList<User> getInterestFollowers(String id, int perPage, int page) throws Exception;
    public abstract boolean followInterest(String id) throws Exception;
    public abstract boolean unfollowInterest(String id) throws Exception;
    public abstract boolean followingInterest(String id) throws Exception;
    
    /*
     * Conversation
     */
    public abstract BHList<Conversation> listConversationFeed(String order, int perPage, int page, Date after, Coordinates location, int radius) throws Exception;
    public abstract BHList<Conversation> listConversations(String order, int perPage, int page) throws Exception;
    public abstract BHList<Conversation> listConversations(String interestId, String order, int perPage, int page) throws Exception;
    public abstract BHList<Conversation> listConversations(String interestId, int radius, Coordinates coord, String order, int perPage, int page) throws Exception;
    public abstract Conversation createConversation(Conversation c) throws Exception;
    public abstract Conversation findConversation(String interestId, String id) throws Exception;
    public abstract BHList<Conversation> listConversationReplies(Conversation c, Date after, int perPage, int page) throws Exception;
    public abstract Conversation replyToConversation(Conversation c) throws Exception;
    public abstract Conversation findConversationReply(String interestId, String conversationId, String id) throws Exception;
    
    /*
     * User
     */
    public abstract User findUser(String name) throws Exception;
    public abstract BHList<Interest> listUserInterests(int perPage, int page) throws Exception;
    public abstract BHList<Conversation> listUserConversations(int perPage, int page) throws Exception;
    public abstract BHList<Interest> listUserInterests(String name, int perPage, int page) throws Exception;
    public abstract BHList<Conversation> listUserConversations(String name, int perPage, int page) throws Exception;
    public abstract BHList<Interest> searchUserInterests(String name) throws Exception;
    
    public abstract BHList<Conversation> activity(int perPage, int page, Date after) throws Exception;
    
    public abstract User getAuthUser() throws Exception;
    public abstract User updateAuthUser(User user) throws Exception;
    
    public abstract User createUser(User user) throws Exception;
}
