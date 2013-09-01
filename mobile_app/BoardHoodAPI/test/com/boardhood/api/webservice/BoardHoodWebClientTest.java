package com.boardhood.api.webservice;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.boardhood.api.BoardHood;
import com.boardhood.api.model.Conversation;
import com.boardhood.api.model.Coordinates;
import com.boardhood.api.model.Interest;
import com.boardhood.api.model.User;

public class BoardHoodWebClientTest {
	private BoardHood client;
	@Before
	public void setUp() throws Exception {
		client = new BoardHoodWebClient();
		client.setCredentials("john", "teste");
		boolean result = client.authenticateUserCredentials();
		assertTrue(result);
	}
	
	@Test
	public void testSearchInterests() {
		try {
			List<Interest> list = client.searchInterests("c");
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testListInterestsStringIntInt() {
		try {
			List<Interest> list = client.listInterests(BoardHood.ORDER_POPULAR, 10, 1);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testListInterestsStringIntCoordinatesIntInt() {
		try {
			Coordinates coord = new Coordinates(-27.86403, -54.4593889);
			List<Interest> list = client.listInterests(BoardHood.ORDER_POPULAR, 30000, coord, 10, 1);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateInterest() {
		try {
			Interest i = client.createInterest(new Interest("Test::"+System.currentTimeMillis()));
			assertTrue(i != null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testFindInterest() {
		try {
			Interest i = client.findInterest("1e03ed55");
			assertTrue(i != null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testGetInterestFollowers() {
		try {
			List<User> list = client.getInterestFollowers("1e03ed55", 20, 1);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFollowInterest() {
		try {
			boolean result = client.followInterest("1e03ed55");
			assertTrue(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testUnfollowInterest() {
		try {
			boolean result = client.unfollowInterest("1e03ed55");
			assertTrue(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFollowingInterest() {
		try {
			boolean result = client.followingInterest("1e03ed55");
			assertFalse(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testListConversationFeed() {
		try {
			List<Conversation> list = client.listConversationFeed(BoardHood.ORDER_POPULAR, 10, 1, null, null, 0);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testListConversations() {
		try {
			List<Conversation> list = client.listConversations(BoardHood.ORDER_POPULAR, 10, 1);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testListConversationsStringStringIntInt() {
		try {
			List<Conversation> list = client.listConversations("1e03ed55", BoardHood.ORDER_POPULAR, 10, 1);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testListConversationsStringIntCoordinatesStringIntInt() {
		try {
			Coordinates coord = new Coordinates(-27.86403, -54.4593889);
			List<Conversation> list = client.listConversations("1e03ed55", 30000, coord, BoardHood.ORDER_DISTANCE, 10, 1);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateConversation() {
		try {
			Coordinates coord = new Coordinates(-27.86403, -54.4593889);
			Interest i = new Interest();
			i.setId("1e03ed55");
			
			Conversation c = new Conversation();
			c.setInterest(i);
			c.setMessage("Just a test");
			c.setLocation(coord);
			
			c = client.createConversation(c);
			assertTrue(c != null);
			assertTrue(c.getId() != null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFindConversation() {
		try {
			Conversation i = client.findConversation("1e03ed55", "bd7a5164");
			assertTrue(i != null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testListConversationReplies() {
		try {
			Interest i = new Interest();
			i.setId("1e03ed55");
			
			Conversation c = new Conversation();
			c.setId("bd7a5164");
			c.setInterest(i);
			
			List<Conversation> list = client.listConversationReplies(c, null, 20, 1);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testReplyToConversation() {
		try {
			Coordinates coord = new Coordinates(-27.86403, -54.4593889);
			Interest i = new Interest();
			i.setId("1e03ed55");
			
			Conversation p = new Conversation();
			p.setId("bd7a5164");
			
			Conversation c = new Conversation();
			c.setInterest(i);
			c.setParent(p);
			c.setMessage("Just a test");
			c.setLocation(coord);
			
			c = client.replyToConversation(c);
			assertTrue(c != null);
			assertTrue(c.getId() != null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFindConversationReply() {
		try {
			Conversation i = client.findConversationReply("1e03ed55", "bd7a5164", "46ed28b6");
			assertTrue(i != null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testFindUser() {
		try {
			User i = client.findUser("john");
			assertTrue(i != null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testListAuthUserInterests() {
		try {
			List<Interest> list = client.listUserInterests(10, 1);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testListAuthUserConversations() {
		try {
			List<Conversation> list = client.listUserConversations(10, 1);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testListUserInterests() {
		try {
			List<Interest> list = client.listUserInterests("john", 10, 1);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testListUserConversations() {
		try {
			List<Conversation> list = client.listUserConversations("john", 10, 1);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testUserSearchInterests() {
		try {
			List<Interest> list = client.searchUserInterests("d");
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testActivity() {
		try {
			List<Conversation> list = client.activity(10, 1, null);
			assertTrue(list.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
