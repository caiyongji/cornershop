package iuv.cns.repo;

import java.util.List;
import java.util.Map;

public interface Bo {
	void regUser(String openId, String whichQr);

	void activeVipCard(Map<String, String> msgMap);

	String getVipCodeByOpenId(String openId);

	void recordEventLog(Map<String, String> msgMap);

	void recordOrder(Map<String, Object> orderMap);

	List<Map<String, String>> getWePayCard(String yyyymmdd);

	boolean updateOrderFHSign(String orderId);

	boolean updateOrderTDSign(String orderId, String openId);

	List<Map<String, String>> selectOrderInfoByOrderId(String orderId);

	List<Map<String, String>> selectOrderInfoTop10(String openId);

	List<Map<String, String>> selectUnorderInfo(String openId);

	String getWish();

	List<String> getPhonesInOrders(String openId);

	boolean isMsgMoreToday(String mobile, String template);

	void storeSnsMsg(String mobile, String template);

	int getTotalPrice(String openId);

	boolean isHurry(String openId);

	boolean sysncGroup(List<Map<String, String>> groupList);

	void emptyGroup();

	void emptyProducts();

	boolean sysncAllProducts(List<Map<String, String>> allProducts);

	List<Map<String, String>> getCatalog();

	List<Map<String, String>> getProductsByCatalog(String catalogId, String sortOrd);

	List<Map<String, String>> getProductsByGroup(String groupId, String sortOrd);

	void emptyUserShopCart(String openId);

	void insertUserShopCart(Map<String, Object> params);

	List<Map<String, String>> getUserShopCart(String openId);

	List<Map<String, String>> getShop();

	List<Map<String, String>> getUserAddress(String openId);

	void insertUserAddress(Map<String, String> params);
	
	List<Map<String, String>> getUserCoupons(Map<String, String> params);
	
	List<Map<String, String>> getUserDirectCoupons(Map<String, String> params);
	
	List<Map<String, String>> getActivityEvents(String totalPrice);
	
	List<Map<String, String>> getPromotionEvents(String totalPrice);
	
	List<Map<String, String>> getProductDetailByIds(List<String> productIds);
	
	List<String> getAddressKeywords();
	
	List<Map<String, String>> getUserCouponsByCard(Map<String, String> params);
	
	void makePreOrder(List<Map<String, String>> list);
	
	void makeRealOrder(Map<String, String> params);
	
	boolean checkRealOrderExist(String outTradeNo);
	
	List<Map<String, String>> getUsrOrders(Map<String, String> params);
	
	List<Map<String, String>> getOrderDetailByOutTradeNo(Map<String, String> params);
}
