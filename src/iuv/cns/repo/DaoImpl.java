package iuv.cns.repo;

import iuv.cns.utils.Constants;
import iuv.cns.utils.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DaoImpl implements Dao {
	private static Log LOG = LogFactory.getLog(DaoImpl.class);
	private final static String NAMESPACE = "cns.wechat.";
	@Autowired
	SqlMapClientTemplate sqlMapClientTemplate;

	/**
	 * 关注时，注册用户
	 * 
	 * @param openId
	 */
	@Override
	public void regUser(String openId, String whichQr) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("openId", openId);
		map.put("whichQr", whichQr);
		try {
			sqlMapClientTemplate.insert(NAMESPACE + "regUser", map);
		} catch (Exception e) {
			LOG.error("注册用户:" + openId, e);
		}
	}

	/**
	 * 激活会员卡，存储会员信息
	 * 
	 * @param param
	 */
	@Override
	public void activeVipCard(Map<String, String> msgMap) {
		try {
			sqlMapClientTemplate.insert(NAMESPACE + "activeVipCard", msgMap);
		} catch (Exception e) {
			LOG.error("activeVipCard:" + paramToLog(msgMap), e);
		}
	}

	@Override
	public void recordEventLog(Map<String, String> msgMap) {
		try {
			JSONObject jsonContent = JSONObject.fromObject(msgMap);
			msgMap.put("jsonContent", jsonContent.toString());
			sqlMapClientTemplate.insert(NAMESPACE + "recordEventLog", msgMap);
		} catch (Exception e) {
			LOG.error("recordEventLog:" + paramToLog(msgMap), e);
		}
	}

	@Override
	public void recordOrder(Map<String, Object> msgMap) {
		try {
			sqlMapClientTemplate.insert(NAMESPACE + "recordOrder", msgMap);
		} catch (Exception e) {
			LOG.error("recordOrder:" + paramsToLog(msgMap), e);
		}
	}

	@Override
	public String getVipCodeByOpenId(String openId) {
		try {
			return (String) sqlMapClientTemplate.queryForObject(NAMESPACE + "getVipCodeByOpenId", openId);
		} catch (Exception e) {
			LOG.error("getVipCodeByOpenId:" + openId, e);
			return "";
		}
	}

	private String paramsToLog(Map<String, Object> msgMap) {
		String result = "";
		Set<String> set = msgMap.keySet();
		for (String key : set) {
			result += "【key:" + key + ",value;" + msgMap.get(key) + "】";
		}
		return result;
	}

	private String paramToLog(Map<String, String> msgMap) {
		String result = "";
		Set<String> set = msgMap.keySet();
		for (String key : set) {
			result += "【key:" + key + ",value;" + msgMap.get(key) + "】";
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getWePayCard(String yyyymmdd) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getWePayCard", yyyymmdd);
		} catch (Exception e) {
			LOG.error("获取现金券营销失败:", e);
		}
		return list;
	}

	@Override
	public boolean updateOrderFHSign(String orderId) {
		try {
			int i = sqlMapClientTemplate.update(NAMESPACE + "updateOrderFHSign", orderId);
			if (i > 0) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("标记发货失败,订单【" + orderId + "】:", e);
		}
		return false;
	}

	@Override
	public boolean updateOrderTDSign(String orderId, String openId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("orderId", orderId);
		map.put("openId", openId);
		try {
			int i = sqlMapClientTemplate.update(NAMESPACE + "updateOrderTDSign", map);
			if (i > 0) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("标记退订失败,订单【" + orderId + "】openid【" + openId + "】:", e);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> selectOrderInfoByOrderId(String orderId) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "selectOrderInfoByOrderId", orderId);
		} catch (Exception e) {
			LOG.error("获取订单信息失败，订单【" + orderId + "】:", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> selectOrderInfoTop10(String openId) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "selectOrderInfoTop10", openId);
		} catch (Exception e) {
			LOG.error("获取近期订单（前十）失败openId【" + openId + "】:", e);
		}
		return list;
	}

	@Override
	public String getWish() {
		String wish = "";
		try {
			wish = (String) sqlMapClientTemplate.queryForObject(NAMESPACE + "getWish");
		} catch (Exception e) {
			LOG.error("获取祝福语失败，启用默认:", e);
		}
		return wish;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getPhonesInOrders(String openId) {
		List<String> list = new ArrayList<String>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getPhonesInOrders", openId);
		} catch (Exception e) {
			LOG.error("获取用户手机号失败openId：" + openId, e);
		}
		return list;
	}

	@Override
	public boolean isMsgMoreToday(String mobile, String template) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("mobile", mobile);
		param.put("template", template);
		try {
			int i = (int) sqlMapClientTemplate.queryForObject(NAMESPACE + "countMsgMoreToday", param);
			if (i < Constants.ALI_MSG_MAX_COUNT) {// 短信平台同一天、统一模板、同一手机号发送短信有上限
				return true;
			}
		} catch (Exception e) {
			LOG.error("判断手机号是否超出发送统计,手机号【" + mobile + "】模板【" + template + "】:", e);
		}
		return false;
	}

	@Override
	public void storeSnsMsg(String mobile, String template) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("mobile", mobile);
		param.put("template", template);
		try {
			sqlMapClientTemplate.insert(NAMESPACE + "storeSnsMsg", param);
		} catch (Exception e) {
			LOG.error("存储发送短信统计信息失败手机【" + mobile + "】模板【" + template + "】:", e);
		}
	}

	@Override
	public int getTotalPrice(String openId) {
		int result = 0;
		try {
			result = (int) sqlMapClientTemplate.queryForObject(NAMESPACE + "getTotalPrice", openId);
		} catch (Exception e) {
			LOG.error("获取用户未发货订单金额,openId【" + openId + "】:", e);
		}
		return result;
	}

	@Override
	public boolean isHurry(String openId) {
		try {
			int i = (int) sqlMapClientTemplate.queryForObject(NAMESPACE + "isHurry", openId);
			if (i > 0) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("判断是否加急,openId【" + openId + "】:", e);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> selectUnorderInfo(String openId) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "selectUnorderInfo", openId);
		} catch (Exception e) {
			LOG.error("获取待收订单失败，openId【" + openId + "】:", e);
		}
		return list;
	}

	@Override
	public boolean sysncGroup(List<Map<String, String>> groupList) {
		try {
			sqlMapClientTemplate.insert(NAMESPACE + "sysncGroup", groupList);
			return true;
		} catch (Exception e) {
			LOG.error("数据库插入分组信息失败:", e);
		}
		return false;
	}

	@Override
	public void emptyGroup() {
		try {
			sqlMapClientTemplate.delete(NAMESPACE + "emptyGroup");
		} catch (Exception e) {
			LOG.error("数据库清空分组信息失败:", e);
		}
	}

	@Override
	public void emptyProducts() {
		try {
			sqlMapClientTemplate.delete(NAMESPACE + "emptyProducts");
		} catch (Exception e) {
			LOG.error("数据库清空全部商品信息失败:", e);
		}
	}

	@Override
	public boolean sysncAllProducts(List<Map<String, String>> allProducts) {
		try {
			sqlMapClientTemplate.insert(NAMESPACE + "sysncAllProducts", allProducts);
			return true;
		} catch (Exception e) {
			LOG.error("数据库插入全部商品详细信息失败:", e);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getCatalog() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getCatalog");
		} catch (Exception e) {
			LOG.error("获取目录失败:", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getProductsByCatalog(String catalogId, String sortOrd) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("catalogId", catalogId);
		params.put("sortOrd", sortOrd);
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getProductsByCatalog", params);
		} catch (Exception e) {
			LOG.error("根据商品分类获取商品列表失败:", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getProductsByGroup(String groupId, String sortOrd) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("groupId", groupId);
		params.put("sortOrd", sortOrd);
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getProductsByGroup", params);
		} catch (Exception e) {
			LOG.error("根据商品分组获取商品列表失败:", e);
		}
		return list;
	}

	@Override
	public void emptyUserShopCart(String openId) {
		try {
			sqlMapClientTemplate.delete(NAMESPACE + "emptyUserShopCart", openId);
		} catch (Exception e) {
			LOG.error("清空用户购物车失败:openId【" + openId + "】", e);
		}
	}

	@Override
	public void insertUserShopCart(Map<String, Object> params) {
		try {
			sqlMapClientTemplate.insert(NAMESPACE + "insertUserShopCart", params);
		} catch (Exception e) {
			LOG.error("刷新插入用户购物车失败:openId【" + params.get("openId") + "】", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getUserShopCart(String openId) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getUserShopCart", openId);
		} catch (Exception e) {
			LOG.error("获取购物车信息失败OPENID【" + openId + "】:", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getShop() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getShop");
		} catch (Exception e) {
			LOG.error("获取商城首页数据失败:", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getUserAddress(String openId) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getUserAddress", openId);
		} catch (Exception e) {
			LOG.error("获取用户地址信息失败openId【" + openId + "】:", e);
		}
		return list;
	}

	@Override
	public void insertUserAddress(Map<String, String> params) {
		try {
			sqlMapClientTemplate.insert(NAMESPACE + "insertUserAddress", params);
		} catch (Exception e) {
			LOG.error("insertUserAddress:" + paramToLog(params), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getUserCoupons(Map<String, String> params) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getUserCoupons", params);
		} catch (Exception e) {
			LOG.error("获取用户优惠券信息失败【" + paramToLog(params) + "】:", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getUserDirectCoupons(Map<String, String> params) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getUserDirectCoupons", params);
		} catch (Exception e) {
			LOG.error("获取用户现金券券信息失败【" + paramToLog(params) + "】:", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getActivityEvents(String totalPrice) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("totalPrice", totalPrice);
		params.put("yyyymmdd", DateUtil.getyyyyMMdd());
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getActivityEvents", params);
		} catch (Exception e) {
			LOG.error("getActivityEvents【" + totalPrice + "】:", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getPromotionEvents(String totalPrice) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("totalPrice", totalPrice);
		params.put("yyyymmdd", DateUtil.getyyyyMMdd());
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getPromotionEvents", params);
		} catch (Exception e) {
			LOG.error("getActivityEvents【" + totalPrice + "】:", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getProductDetailByIds(List<String> productIds) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("productIds", productIds);
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getProductDetailByIds", params);
		} catch (Exception e) {
			LOG.error("getProductDetailByIds【" + productIds.toString() + "】:", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAddressKeywords() {
		List<String> list = new ArrayList<String>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getAddressKeywords");
		} catch (Exception e) {
			LOG.error("获取可配送区域关键词失败:", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getUserCouponsByCard(Map<String, String> params) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getUserCouponsByCard", params);
		} catch (Exception e) {
			LOG.error("获取用户优惠券信息getUserCouponsByCard失败【" + paramToLog(params) + "】:", e);
		}
		return list;
	}

	@Override
	public void makePreOrder(List<Map<String, String>> detailList) {
		try {
			sqlMapClientTemplate.insert(NAMESPACE + "makePreOrder", detailList);
		} catch (Exception e) {
			LOG.error("生成预处理订单makePreOrder失败【" + detailList.toString() + "】：", e);
		}
	}

	@Override
	public void makeRealOrder(Map<String, String> params) {
		try {
			sqlMapClientTemplate.insert(NAMESPACE + "makeRealOrder", params);
		} catch (Exception e) {
			LOG.error("生成真正订单（真实支付订单）makeRealOrder失败【" + paramToLog(params) + "】：", e);
		}
	}

	@Override
	public boolean checkRealOrderExist(String outTradeNo) {
		try {
			int i = (int) sqlMapClientTemplate.queryForObject(NAMESPACE + "checkRealOrderExist", outTradeNo);
			if (i >0) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("判断单号是否存在【" + outTradeNo + "】", e);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getUsrOrders(Map<String, String> params) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getUsrOrders", params);
		} catch (Exception e) {
			LOG.error("getUsrOrders【用户自行查询】获取用户订单信息失败【" + paramToLog(params) + "】:", e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> getOrderDetailByOutTradeNo(Map<String, String> params) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			list = sqlMapClientTemplate.queryForList(NAMESPACE + "getOrderDetailByOutTradeNo", params);
		} catch (Exception e) {
			LOG.error("获取单个订单详细信息（用于订单打印）【" + paramToLog(params) + "】:", e);
		}
		return list;
	}
}
