/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: PushService.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	12 Sep,2015
 */
package net.wit.mobile.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jodd.json.JsonParser;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.cache.CacheUtil;
import net.wit.mobile.service.IPushService;
import net.wit.mobile.util.rong.ApiHttpClient;
import net.wit.mobile.util.rong.models.FormatType;
import net.wit.mobile.util.rong.models.Message;
import net.wit.mobile.util.rong.models.NoticeMessage;
import net.wit.mobile.util.rong.models.SdkHttpResult;
import net.wit.mobile.util.rong.models.SystemMessage;
import net.wit.mobile.util.rong.models.TxtMessage;
import net.wit.vo.SystemMessageVO;

import org.springframework.stereotype.Service;

/**
 * 
 * @author: weihuang.peng
 * @version Revision: 0.0.1
 * @Date: 12 Sep,2015
 */
@Service("pushService")
@SuppressWarnings("unchecked")
public class PushService implements IPushService {

	private String appKey = "uwd1c0sxd3xi1";
	private String appSecret = "D4PWj4mulnY3U";
	private static final String SYSTEM_MESSAGE_PREFIX = "tenant_";
	private static final String SYS_MSG_SWITCH = "SYS_MSG_SWITCH";

	@Override
	public String getToken(NToken nToken) {
		SdkHttpResult result = null;

		try {
			result = ApiHttpClient.getToken(appKey, appSecret,
					nToken.getMemberId(), nToken.getNickName(),
					nToken.getHeadImg());
		} catch (Exception e) {
			result = null;
		}

		if (result == null) {
			return null;
		}

		String json = result.getResult();

		Map<String, String> map = new JsonParser().parse(json, Map.class);

		String tokenId = map.get("token");
		return tokenId;
	}

	@Override
	public String getToken(String id, String username, String portraitUri) {
		SdkHttpResult result = null;
		try {
			result = ApiHttpClient.getToken(appKey, appSecret, id, username,
					portraitUri);
		} catch (Exception e) {
			result = null;
		}

		if (result == null) {
			return null;
		}

		String json = result.getResult();

		Map<String, String> map = new JsonParser().parse(json, Map.class);

		String tokenId = map.get("token");
		return tokenId;
	}

	@Override
	public boolean publishMessage(String fromUserId, List<String> toUserIds,
			TxtMessage msg) {
		SdkHttpResult result = null;
		try {
			result = ApiHttpClient.publishMessage(appKey, appSecret,
					fromUserId, toUserIds, msg);
			if (result == null) {
				return false;
			}

			if (result.getHttpCode() == 200) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public boolean refreshToken(String fromUserId, String userName,
			String portraitUri) {
		SdkHttpResult result = null;

		try {
			result = ApiHttpClient.refreshUser(appKey, appSecret, fromUserId,
					userName, portraitUri, FormatType.json);
			if (result == null) {
				return false;
			}

			if (result.getHttpCode() == 200) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public boolean publishNoticeMessage(String fromUserId,
			List<String> toUserIds, String title, String content) {
		SdkHttpResult result = null;
		Message msg = new NoticeMessage(content);
		try {
			result = ApiHttpClient.publishSystemMessage(appKey, appSecret,
					fromUserId, toUserIds, msg);
			if (result == null)
				return false;
			if (result.getHttpCode() == 200)
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	@Override
	public boolean publishSystemMessage(Tenant tenant, Member member,
			SystemMessage message) {
		if (CacheUtil.getParamValueByName(SYS_MSG_SWITCH).equals("1")) {
			SdkHttpResult result = null;
			try {
				List<String> toUserIds = new ArrayList<String>();
				toUserIds.add(member.getId() + "");
				String fromUserId = SYSTEM_MESSAGE_PREFIX + tenant.getId();
				result = ApiHttpClient.publishSystemMessage(appKey, appSecret,
						fromUserId, toUserIds, message);
				if (result == null)
					return false;
				if (result.getHttpCode() == 200)
					return true;
			} catch (Exception e) {
				return false;
			}
			return false;
		} else
			return false;
	}
	
	@Override
	public boolean publishSystemMessage(SystemMessageVO vo) {
		if (CacheUtil.getParamValueByName(SYS_MSG_SWITCH).equals("1")) {
			SdkHttpResult result = null;
			try {
				List<String> toUserIds = new ArrayList<String>();
				toUserIds.add(vo.getMember().getId() + "");
				String fromUserId = SYSTEM_MESSAGE_PREFIX + vo.getTenant().getId();
				result = ApiHttpClient.publishSystemMessage(appKey, appSecret,
						fromUserId, toUserIds, vo.getMessage());
				if (result == null)
					return false;
				if (result.getHttpCode() == 200)
					return true;
			} catch (Exception e) {
				return false;
			}
			return false;
		} else
			return false;
	}


}