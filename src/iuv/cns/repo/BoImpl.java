package iuv.cns.repo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoImpl implements Bo {
	@Autowired
	Dao dao;

	@Override
	public void regUser(String openId, String whichQr) {
		dao.regUser(openId, whichQr);
	}

	@Override
	public void activeVipCard(Map<String, String> msgMap) {
		dao.activeVipCard(msgMap);
	}

	@Override
	public String getVipCodeByOpenId(String openId) {
		return dao.getVipCodeByOpenId(openId);
	}

	@Override
	public void recordEventLog(Map<String, String> msgMap) {
		dao.recordEventLog(msgMap);
	}

	@Override
	public void recordOrder(Map<String, Object> msgMap) {
		dao.recordOrder(msgMap);
	}

	@Override
	public List<Map<String, String>> getWePayCard(String yyyymmdd) {
		return dao.getWePayCard(yyyymmdd);
	}

	@Override
	public boolean updateOrderFHSign(String orderId) {
		return dao.updateOrderFHSign(orderId);
	}

	@Override
	public boolean updateOrderTDSign(String orderId, String openId) {
		return dao.updateOrderTDSign(orderId, openId);
	}

	@Override
	public List<Map<String, String>> selectOrderInfoByOrderId(String orderId) {
		return dao.selectOrderInfoByOrderId(orderId);
	}

	@Override
	public List<Map<String, String>> selectOrderInfoTop10(String openId) {
		return dao.selectOrderInfoTop10(openId);
	}

	@Override
	public String getWish() {
		return dao.getWish();
	}

	@Override
	public List<String> getPhonesInOrders(String openId) {
		return dao.getPhonesInOrders(openId);
	}

	@Override
	public boolean isMsgMoreToday(String mobile, String template) {
		return dao.isMsgMoreToday(mobile, template);
	}

	@Override
	public void storeSnsMsg(String mobile, String template) {
		dao.storeSnsMsg(mobile, template);
	}

	@Override
	public int getTotalPrice(String openId) {
		return dao.getTotalPrice(openId);
	}

	@Override
	public boolean isHurry(String openId) {
		return dao.isHurry(openId);
	}

	@Override
	public List<Map<String, String>> selectUnorderInfo(String openId) {
		return dao.selectUnorderInfo(openId);
	}

	@Override
	public boolean sysncGroup(List<Map<String, String>> groupList) {
		return dao.sysncGroup(groupList);
	}

	@Override
	public void emptyGroup() {
		dao.emptyGroup();
	}

	@Override
	public void emptyProducts() {
		dao.emptyProducts();
	}

	@Override
	public boolean sysncAllProducts(List<Map<String, String>> allProducts) {
		return dao.sysncAllProducts(allProducts);
	}

	@Override
	public List<Map<String, String>> getCatalog() {
		return dao.getCatalog();
	}

	@Override
	public List<Map<String, String>> getProductsByCatalog(String catalogId, String sortOrd) {
		return dao.getProductsByCatalog(catalogId, sortOrd);
	}

	@Override
	public List<Map<String, String>> getProductsByGroup(String groupId, String sortOrd) {
		return dao.getProductsByGroup(groupId, sortOrd);
	}

	@Override
	public void emptyUserShopCart(String openId) {
		dao.emptyUserShopCart(openId);
	}

	@Override
	public void insertUserShopCart(Map<String, Object> params) {
		dao.insertUserShopCart(params);
	}

	@Override
	public List<Map<String, String>> getUserShopCart(String openId) {
		return dao.getUserShopCart(openId);
	}

	@Override
	public List<Map<String, String>> getShop() {
		return dao.getShop();
	}

	@Override
	public List<Map<String, String>> getUserAddress(String openId) {
		return dao.getUserAddress(openId);
	}

	@Override
	public void insertUserAddress(Map<String, String> params) {
		dao.insertUserAddress(params);
	}

	@Override
	public List<Map<String, String>> getUserCoupons(Map<String, String> params) {
		return dao.getUserCoupons(params);
	}
	
	@Override
	public List<Map<String, String>> getUserDirectCoupons(Map<String, String> params) {
		return dao.getUserDirectCoupons(params);
	}

	@Override
	public List<Map<String, String>> getActivityEvents(String totalPrice) {
		return dao.getActivityEvents(totalPrice);
	}

	@Override
	public List<Map<String, String>> getPromotionEvents(String totalPrice) {
		return dao.getPromotionEvents(totalPrice);
	}

	@Override
	public List<Map<String, String>> getProductDetailByIds(List<String> productIds) {
		return dao.getProductDetailByIds(productIds);
	}

	@Override
	public List<String> getAddressKeywords() {
		return dao.getAddressKeywords();
	}

	@Override
	public List<Map<String, String>> getUserCouponsByCard(Map<String, String> params) {
		return dao.getUserCouponsByCard(params);
	}

	@Override
	public void makePreOrder(List<Map<String, String>> list) {
		dao.makePreOrder(list);
	}

	@Override
	public void makeRealOrder(Map<String, String> params) {
		dao.makeRealOrder(params);
	}

	@Override
	public boolean checkRealOrderExist(String outTradeNo) {
		return dao.checkRealOrderExist(outTradeNo);
	}

	@Override
	public List<Map<String, String>> getUsrOrders(Map<String, String> params) {
		return dao.getUsrOrders(params);
	}

	@Override
	public List<Map<String, String>> getOrderDetailByOutTradeNo(Map<String, String> params) {
		return dao.getOrderDetailByOutTradeNo(params);
	}
}
