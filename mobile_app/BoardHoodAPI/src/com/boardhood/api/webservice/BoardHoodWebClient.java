package com.boardhood.api.webservice;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONObject;

import com.boardhood.api.BoardHood;
import com.boardhood.api.exception.BoardHoodException;
import com.boardhood.api.exception.ValidationException;
import com.boardhood.api.model.Conversation;
import com.boardhood.api.model.Coordinates;
import com.boardhood.api.model.Interest;
import com.boardhood.api.model.User;
import com.boardhood.api.util.BHList;
import com.boardhood.api.util.DateUtils;
import com.boardhood.api.util.CacheProvider;
import com.boardhood.api.util.rest.Request;
import com.boardhood.api.util.rest.Response;
import com.boardhood.api.util.rest.RestClient;
import com.boardhood.api.util.rest.errors.UnauthorizedException;

public class BoardHoodWebClient implements BoardHood {    
    private static final String BASE_URL = "http://localhost:5000/";
    
	private RestClient restClient;
	private ResponseParser responseParser;
	
	protected CacheProvider<String, String> cache;
	protected boolean cacheEnabled = false;
	
	private UsernamePasswordCredentials credentials;
	private String apiKey;
	
	public BoardHoodWebClient() {
		restClient = new RestClient(BASE_URL);
		responseParser = new ResponseParser();
	}
	
	public BoardHoodWebClient(String baseUrl) {
		restClient = new RestClient(baseUrl);
		responseParser = new ResponseParser();
	}
	
	public void setCacheProvider(CacheProvider<String, String> cache) {
		this.cache = cache;
		this.cacheEnabled = true;
	}
	
	public CacheProvider<String, String> getCacheProvider() {
		return cache;
	}
	
	@Override
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public void setCredentials(String username, String password) {
		credentials = new UsernamePasswordCredentials(username, password);
	}

	@Override
	public boolean authenticateUserCredentials() throws Exception {
		try {
			restClient.deleteExtraParam("auth_token");
			restClient.deleteExtraParam("api_key");
			
			restClient.setCredentials(credentials);
			Response response = restClient.get(new Request(""));
			restClient.addExtraParam("auth_token", response.getAuthToken());
			return true;
		} catch (UnauthorizedException e) {
			return false;
		} catch (Exception e) {
			throw e;
		} finally {
			restClient.clearCredentials();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BHList<Interest> searchInterests(String name) throws Exception {
		String key = "InterestsSearch" + name;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("interests/search/" + URLEncoder.encode(name));
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 10 * 60 * 1000); //10min
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return searchInterests(name);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseInterests(getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseInterests(content);
	}

	@Override
	public BHList<Interest> listInterests(String order, int perPage, int page)
			throws Exception {
		String key = "Interests" + order + "Page" + page;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("interests");
				request.addParam("order", order);
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 60*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return listInterests(order, perPage, page);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseInterests(getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseInterests(content);
	}

	@Override
	public BHList<Interest> listInterests(String order, int radius,
			Coordinates coord, int perPage, int page) throws Exception {
		String key = "Interests";
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("interests");
				request.addParam("order", order);
				request.addParam("radius", radius);
				request.addParam("lat", coord.getLat());
				request.addParam("lon", coord.getLon());
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 60*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return listInterests(order, radius, coord, perPage, page);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseInterests(getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseInterests(content);
	}

	@Override
	public Interest createInterest(Interest i) throws Exception {
		try {
			Request request = new Request("interests");
			request.addParam("name", i.getName());
			
			Response response = restClient.post(request);
			return responseParser.parseInterest(response.getJSONContent());
		} catch (UnauthorizedException e) {
			if (authenticateUserCredentials())
				return createInterest(i);
			else
				throw new BoardHoodException("Bad Credentials");
		}
	}

	@Override
	public Interest findInterest(String id) throws Exception {
		String key = "Interest" + id;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Response response = restClient.get(new Request("interests/" + id));
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 30*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return findInterest(id);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseInterest(getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseInterest(content);
	}

	@Override
	public BHList<User> getInterestFollowers(String id, int perPage, int page) throws Exception {
		String key = "Interest" + id + "Followers" + "Page" + page;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("interests/" + id + "/followers");
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 30*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return getInterestFollowers(id, perPage, page);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseUsers("followers", getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseUsers("followers", content);
	}

	@Override
	public boolean followInterest(String id) throws Exception {
		try {
			Response response = restClient.post(new Request("interests/" + id + "/followers/me"));
			return (response.getCode() == 201);
		} catch (UnauthorizedException e) {
			if (authenticateUserCredentials())
				return followInterest(id);
			else
				throw new BoardHoodException("Bad Credentials");
		}
	}

	@Override
	public boolean unfollowInterest(String id) throws Exception {
		try {
			Response response = restClient.delete(new Request("interests/" + id + "/followers/me"));
			return (response.getCode() == 204);
		} catch (UnauthorizedException e) {
			if (authenticateUserCredentials())
				return unfollowInterest(id);
			else
				throw new BoardHoodException("Bad Credentials");
		}
	}

	@Override
	public boolean followingInterest(String id) throws Exception {
		try {
			Response response = restClient.get(new Request("interests/" + id + "/followers/me"));
			return (response.getCode() == 204);
		} catch (UnauthorizedException e) {
			if (authenticateUserCredentials())
				return followingInterest(id);
			else
				throw new BoardHoodException("Bad Credentials");
		}
	}
	
	@Override
	public BHList<Conversation> listConversationFeed(String order, int perPage, 
			int page, Date after, Coordinates location, int radius) throws Exception {
		String key = "ConversationFeed" + order + "Page" + page;
		JSONObject content = null;
		
		if (content == null) {
			try {
				Request request = new Request("conversations/feed");
				request.addParam("order", order);
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				if (after != null) {
					request.addParam("after", DateUtils.toIsoFormat(after));
				}
				
				if (location != null) {
					request.addParam("lat", location.getLat());
					request.addParam("lon", location.getLon());
				}
				
				if (radius != 0) {
					request.addParam("radius", radius);
				}
				
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 15*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return listConversationFeed(order, perPage, page, after, location, radius);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseConversations("conversations", getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseConversations("conversations", content);
	}
	
	@Override
	public BHList<Conversation> listConversations(String order, int perPage, int page) 
			throws Exception {
		String key = "Conversations" + order + "Page" + page;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("/conversations");
				request.addParam("order", order);
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 60*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return listConversations(order, perPage, page);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseConversations("conversations", getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseConversations("conversations", content);
	}

	@Override
	public BHList<Conversation> listConversations(String interestId,
			String order, int perPage, int page) throws Exception {
		String key = "Interest" + interestId + "Conversations" + order + "Page" + page;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("interests/" + interestId + "/conversations");
				request.addParam("order", order);
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 30*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return listConversations(interestId, order, perPage, page);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseConversations("conversations", getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseConversations("conversations", content);
	}

	@Override
	public BHList<Conversation> listConversations(String interestId, int radius,
			Coordinates coord, String order, int perPage, int page)
			throws Exception {
		String key = "Interest" + interestId + "Conversations";
		JSONObject content = null;
		
		if (content == null) {
			try {
				Request request = new Request("interests/" + interestId + "/conversations");
				request.addParam("order", order);
				request.addParam("radius", radius);
				request.addParam("lat", coord.getLat());
				request.addParam("lon", coord.getLon());
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString());
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return listConversations(interestId, radius, coord, order, perPage, page);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseConversations("conversations", getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseConversations("conversations", content);
	}

	@Override
	public Conversation createConversation(Conversation c) throws Exception {
		try {
			Request request = new Request("interests/" + c.getInterest().getId() + "/conversations");
			request.addParam("message", c.getMessage());
			
			if (c.getLocation() != null) {
				request.addParam("lat", c.getLocation().getLat());
				request.addParam("lon", c.getLocation().getLon());
			}
			
			Response response = restClient.post(request);
			return responseParser.parseConversation(response.getJSONContent());
		} catch (UnauthorizedException e) {
			if (authenticateUserCredentials())
				return createConversation(c);
			else
				throw new BoardHoodException("Bad Credentials");
		}
	}

	@Override
	public Conversation findConversation(String interestId, String id)
			throws Exception {
		String key = "Interest" + interestId + "Conversation" + id;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("interests/" + interestId + "/conversations/" + id);
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 30*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return findConversation(interestId, id);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseConversation(getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseConversation(content);
	}

	@Override
	public BHList<Conversation> listConversationReplies(Conversation c, Date after,
			int perPage, int page) throws Exception {
		String key = "Conversation" + c.getId() + "RepliesPage" + page;
		JSONObject content = null;
		
		if (content == null) {
			try {
				Request request = new Request("interests/" + c.getInterest().getId() 
						+ "/conversations/" + c.getId() + "/replies");
				
				if (after != null)
					request.addParam("after", DateUtils.toIsoFormat(after));
				
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				Response response = restClient.get(request);
				if (response.getCode() == 404) {
					return null;
				}
				
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 10*60*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return listConversationReplies(c, after, perPage, page);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseConversations("replies", getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseConversations("replies", content);
	}

	@Override
	public Conversation replyToConversation(Conversation c) throws Exception {
		try {
			Request request = new Request("interests/" + c.getInterest().getId() + "/conversations/" + c.getParent().getId() + "/replies");
			request.addParam("message", c.getMessage());
			
			if (c.getLocation() != null) {
				request.addParam("lat", c.getLocation().getLat());
				request.addParam("lon", c.getLocation().getLon());
			}
			
			Response response = restClient.post(request);
			return responseParser.parseConversation(response.getJSONContent());
		} catch (UnauthorizedException e) {
			if (authenticateUserCredentials())
				return replyToConversation(c);
			else
				throw new BoardHoodException("Bad Credentials");
		}
	}

	@Override
	public Conversation findConversationReply(String interestId,
			String conversationId, String id) throws Exception {
		String key = "Interest" + interestId + "Conversation" + conversationId + "Reply" + id;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("interests/" + interestId + "/conversations/" + conversationId + "/replies/" + id);
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 60*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return findConversationReply(interestId, conversationId, id);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseConversation(getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseConversation(content);
	}

	@Override
	public User findUser(String name) throws Exception {
		String key = "User" + name;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("users/" + name);
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 60*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return findUser(name);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseUser(getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseUser(content);
	}
	
	@Override
	public User getAuthUser() throws Exception {
		JSONObject content = null;
		String key = "AuthUser";
		
		if (content == null) {
			try {
				Request request = new Request("user");
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString());
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return getAuthUser();
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseUser(getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseUser(content);
	}
	
	@Override
	public BHList<Interest> listUserInterests(int perPage, int page) throws Exception {
		String key = "AuthUserInterestsPage" + page;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("user/interests");
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 60*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return listUserInterests(perPage, page);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseUserInterests(getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseUserInterests(content);
	}

	@Override
	public BHList<Conversation> listUserConversations(int perPage, int page) throws Exception {
		String key = "AuthUserConversationsPage" + page;
		JSONObject content = null;
		
		if (content == null) {
			try {
				Request request = new Request("user/conversations");
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 60*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return listUserConversations(perPage, page);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseConversations("conversations", getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseConversations("conversations", content);
	}

	@Override
	public BHList<Interest> listUserInterests(String name, int perPage, int page) throws Exception {
		String key = "User" + name + "InterestsPage" + page;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("users/" + name + "/interests");
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 60*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return listUserInterests(name, perPage, page);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseUserInterests(getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseUserInterests(content);
	}

	@Override
	public BHList<Conversation> listUserConversations(String name, int perPage, int page)
			throws Exception {
		String key = "User" + name + "ConversationsPage" + page;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("users/" + name + "/conversations");
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 60*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return listUserConversations(name, perPage, page);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseConversations("conversations", getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseConversations("conversations", content);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BHList<Interest> searchUserInterests(String name) throws Exception {
		String key = "AuthUserInterests" + name;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("user/interests/search/" + URLEncoder.encode(name));
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 60*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return searchUserInterests(name);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseUserInterests(getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseUserInterests(content);
	}
	
	@Override
	public BHList<Conversation> activity(int perPage, int page, Date after) 
			throws Exception {
		String key = "ActivityPage" + page;
		JSONObject content = getFromMemCache(key);
		
		if (content == null) {
			try {
				Request request = new Request("user/activity");
				request.addParam("per_page", perPage);
				request.addParam("page", page);
				
				if (after != null)
					request.addParam("after", DateUtils.toIsoFormat(after));
				
				Response response = restClient.get(request);
				content = response.getJSONContent();
				
				if (cacheEnabled) {
					cache.put(key, content.toString(), 10*1000);
				}
			} catch (UnauthorizedException e) {
				if (authenticateUserCredentials())
					return activity(perPage, page, after);
				else
					throw new BoardHoodException("Bad Credentials");
			} catch (IOException e) {
				return responseParser.parseConversations("conversations", getFromCache(key, e), true);
			}
		}
		
		return responseParser.parseConversations("conversations", content);
	}
	
	@Override
	public User updateAuthUser(User user) throws Exception {
		try {
			Request request = new Request("user");
			request.addParam("name", user.getName());
			request.addParam("email", user.getEmail());
			
			if (user.getPassword() != null && !user.getPassword().equals("")) {
				request.addParam("password", user.getPassword());
			}
			
			Response response;
			
			if (user.getNewAvatar() != null) {
				ByteArrayBody bab = new ByteArrayBody(user.getNewAvatar(), "avatar.jpg");
				request.addByteArray("avatar", bab);
				response = restClient.putMultipart(request);
			} else {
				response = restClient.put(request);
			}
			
			if (response.getCode() == 422)
				throw new ValidationException(responseParser.parseValidationErrors("errors", response.getJSONContent()));
			if (response.getCode() != 200)
				throw new BoardHoodException(response.getJSONContent().getString("message"));
			
			return responseParser.parseUser(response.getJSONContent());
		} catch (UnauthorizedException e) {
			if (authenticateUserCredentials())
				return updateAuthUser(user);
			else
				throw new BoardHoodException("Bad Credentials");
		}
	}
	
	@Override
	public User createUser(User user) throws Exception {
		try {
			restClient.addExtraParam("api_key", apiKey);
			
			Request request = new Request("users");
			request.addParam("name", user.getName());
			request.addParam("email", user.getEmail());
			request.addParam("password", user.getPassword());
			
			Response response;
			
			if (user.getNewAvatar() != null) {
				ByteArrayBody bab = new ByteArrayBody(user.getNewAvatar(), "avatar.jpg");
				request.addByteArray("avatar", bab);
				response = restClient.postMultipart(request);
			} else {
				response = restClient.post(request);
			}
			
			if (response.getCode() == 422)
				throw new ValidationException(responseParser.parseValidationErrors("errors", response.getJSONContent()));
			if (response.getCode() != 201)
				throw new BoardHoodException(response.getJSONContent().getString("message"));
			
			return responseParser.parseUser(response.getJSONContent());
		} catch (UnauthorizedException e) {
			if (authenticateUserCredentials())
				return updateAuthUser(user);
			else
				throw new BoardHoodException("Bad Credentials");
		}
	}
	
	private JSONObject getFromCache(String key, Exception e) throws Exception {
		String content = null;
		if (cacheEnabled) {
			content = cache.get(key, true);
		}
		
		if (content != null) {
			return new JSONObject(content);
		} else {
			throw e;
		}
	}
	
	private JSONObject getFromMemCache(String key) throws Exception {
		String content = null;
		if (cacheEnabled) {
			content = cache.get(key, false);
		}
		
		if (content != null) {
			return new JSONObject(content);
		} else {
			return null;
		}
	}
}
