package com.expenses.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.dozer.Mapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expenses.entity.ExpensesForEachEntity;
import com.expenses.entity.ProductEntity;
import com.expenses.entity.UserEntity;
import com.expenses.pojo.DailyExpensesReqVO;
import com.expenses.pojo.DailyExpensesRespVO;
import com.expenses.pojo.ProductDetailsVO;
import com.expenses.pojo.ResultVO;
import com.expenses.pojo.UserAndPricePOJO;
import com.expenses.pojo.UserDtlVo;
import com.expenses.repo.ExpensesForEachRepo;
import com.expenses.repo.ProductRepo;
import com.expenses.repo.UserRepo;
import com.expenses.service.DailyExpensesService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class DailyExpensesServiceImpl implements DailyExpensesService {
	
	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
	private ExpensesForEachRepo expensesForEachRepo; 	
	
	@Autowired
	private UserRepo userRepo; 
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private Mapper mapper;
	
	

	@Transactional
	@Override
	public DailyExpensesRespVO saveDetails(DailyExpensesReqVO dailyExpensesReqVO) {

		DailyExpensesRespVO dailyExpensesRespVO = new DailyExpensesRespVO();
		
		if (dailyExpensesReqVO == null) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500_ID", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		if (dailyExpensesReqVO.getBuyerUserUuid() == null || dailyExpensesReqVO.getBuyerUserUuid().isBlank()) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500_ID", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		if (dailyExpensesReqVO.getContriUserDtls() == null || dailyExpensesReqVO.getContriUserDtls().isBlank()) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500_ID", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		if (dailyExpensesReqVO.getProduct() == null || dailyExpensesReqVO.getProduct().isBlank()) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500_ID", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		if (dailyExpensesReqVO.getTotalPrice() <= 0) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500_ID", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
				
		try {
			
			JSONArray contriUserArray = new JSONArray(dailyExpensesReqVO.getContriUserDtls());
			
			System.err.println("dailyExpensesReqVO:- " + dailyExpensesReqVO);

			System.err.println("contriUserArray:- " + contriUserArray.toString());
			
			// Saving Buyer Detail....
			Optional<UserEntity> userEntityOptional = userRepo.getUserDetail(dailyExpensesReqVO.getBuyerUserUuid());
			if (userEntityOptional.isEmpty()) {
					dailyExpensesRespVO.setResultVO(new ResultVO("404_BUYER", "Invalid user !!!", true));
					return dailyExpensesRespVO;
			}
			UserEntity userEntity = userEntityOptional.get();
		
//			// Saving current product for current buyer....
			ProductEntity productEntity = new ProductEntity(); 
			productEntity.setBuyerName(userEntity.getName());
			productEntity.setBuyerUserUuid(userEntity.getUserUuid());
			productEntity.setBuyDate(new Date());
			productEntity.setProduct(dailyExpensesReqVO.getProduct());
			productEntity.setProductPrice(dailyExpensesReqVO.getTotalPrice());
			productEntity.setContriUsers(dailyExpensesReqVO.getContriUserDtls());
			productEntity.setFinalStatus(0);
			productEntity.setPaid(0);
			
			productEntity = productRepo.save(productEntity);
			
//			// Saving Contribution on each....
			List<ExpensesForEachEntity> expensesForEachEntities = new ArrayList<ExpensesForEachEntity>();
			
			for (int i=0; i < contriUserArray.length(); i++) {
				JSONObject user = contriUserArray.getJSONObject(i);	
				System.err.println("User:- " + user.toString());
				String uuid = user.getString("name");
				int price = user.getInt("code");
				
				// fetching user detail to get details.....
				Optional<UserEntity> contriUserOptional = userRepo.getUserDetail(uuid);
				if (contriUserOptional.isEmpty()) {
					dailyExpensesRespVO.setResultVO(new ResultVO("404_CONTRI_USER", "Invalid user !!!", true));
					return dailyExpensesRespVO;
				}
				
				ExpensesForEachEntity expensesForEachEntity = new ExpensesForEachEntity();
				expensesForEachEntity.setProductSrno(productEntity.getProductSrno());
				expensesForEachEntity.setBuyerUserUuid(productEntity.getBuyerUserUuid());
				expensesForEachEntity.setContriUserUuid(uuid);
				expensesForEachEntity.setPrice(price);
				expensesForEachEntity.setBuyDate(productEntity.getBuyDate());
				expensesForEachEntity.setBuyerName(productEntity.getBuyerName());
				expensesForEachEntity.setContriUserName(contriUserOptional.get().getName());
				
				expensesForEachEntities.add(expensesForEachEntity);
			}
			
			if (expensesForEachEntities != null && expensesForEachEntities.size() > 0) {
				expensesForEachRepo.saveAll(expensesForEachEntities);
			}
			
			// Fetching Updated result...
			dailyExpensesRespVO = getCompleteDetails(dailyExpensesReqVO);
			
			dailyExpensesRespVO.setResultVO(new ResultVO("200", "Details Saved Successfully !!!", true));
			return dailyExpensesRespVO;
		}
		catch(Exception e) {
			e.printStackTrace();
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Something Went Wrong !!!", true));
			return dailyExpensesRespVO;
		}
	}



	@Transactional
	@Override
	public ResultVO addUser(UserDtlVo userDtlVo) {
		
		if (userDtlVo == null) {
			return new ResultVO("500", "Imroper Data !!!", true);
		}
		if (userDtlVo.getName() == null || userDtlVo.getName().isBlank()) {
			return new ResultVO("500", "User Name cannot be empty !!!", true);
		}
		if (userDtlVo.getEmail() == null || userDtlVo.getEmail().isBlank()) {
			return new ResultVO("500", "User Email cannot be empty !!!", true);
		}
		if (userDtlVo.getMobile() == null || userDtlVo.getMobile().isBlank()) {
			return new ResultVO("500", "User Mobile cannot be empty !!!", true);
		}				
		
		ResultVO resultVO = null;
				
		try {
			//  Fetching details using Email....
			Optional<UserEntity> userEntityOptional = userRepo.getUserDtlByEmail(userDtlVo.getEmail());
			if (userEntityOptional.isPresent()) {
				resultVO = new ResultVO("USER_ALREADY_EXIST", "User with same email already exist !!!", true);
				resultVO.setUserUuid(userEntityOptional.get().getUserUuid());
				return resultVO;
			}
			
			//  Fetching details using Mobile....
			userEntityOptional = userRepo.getUserDtlByMobile(userDtlVo.getMobile());
			if (userEntityOptional.isPresent()) {
				resultVO = new ResultVO("USER_ALREADY_EXIST", "User with same mobile already exist !!!", true);
				resultVO.setUserUuid(userEntityOptional.get().getUserUuid());
				return resultVO;
			}
			
			// Craeting new User....
			UserEntity userEntity = new UserEntity();
			userEntity.setName(userDtlVo.getName());
			userEntity.setEmail(userDtlVo.getEmail());
			userEntity.setMobile(userDtlVo.getMobile());
			userEntity.setFinalStatus(1);
			
			userEntity = userRepo.save(userEntity);
			
			resultVO = new ResultVO("200", "User Created Successfully !!!", true);
			
			resultVO.setUserUuid(userEntity.getUserUuid());
			
			return resultVO;
		}
		catch(Exception e) {
			e.printStackTrace();
			return new ResultVO("500", "Something Went Wrong !!!", true);
		}
	}



	@Override
	public DailyExpensesRespVO getCompleteDetails(DailyExpensesReqVO dailyExpensesReqVO) {
		DailyExpensesRespVO dailyExpensesRespVO = new DailyExpensesRespVO();
		
		if (dailyExpensesReqVO == null) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		if (dailyExpensesReqVO.getBuyerUserUuid() == null) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		
		List<ProductDetailsVO> productDetailsVOs = new ArrayList<ProductDetailsVO>();
		String whereCondition = "";
				
		try {
			
			// If from date is requested then add condition for that.....
			if (dailyExpensesReqVO.getFromDate() != null) {
				whereCondition += (" AND a.buyDate >=" + dailyExpensesReqVO.getBuyDate());
			}
			// If to date is requested then add condition for that.....
			if (dailyExpensesReqVO.getToDate() != null) {
				whereCondition += (" AND a.buyDate <=" + dailyExpensesReqVO.getToDate());
			}
			
			// *********** ALL PRODUCT LIST **************
			// creating query dynamically to fetch all product details list between mentioned dates....
			String queryString = " SELECT new com.expenses.pojo.ProductDetailsVO(a.productSrno, a.buyerUserUuid, a.buyerName, a.buyDate, a.product, a.productPrice) "
					+ " FROM ProductEntity a WHERE a.finalStatus != 2 AND a.buyerUserUuid = '" + dailyExpensesReqVO.getBuyerUserUuid() + "' " + whereCondition;
			
			Query query = entityManager.createQuery(queryString, ProductDetailsVO.class);
			
			productDetailsVOs = query.getResultList();
						
			// saving all product details list to response POJO.....
			dailyExpensesRespVO.setProductDetailsVOs(productDetailsVOs);
			
			entityManager.clear();

			
			/*
			
			// ********* FETCHING DETAILS THAT HOW MUCH OTHER USERS WILL PAY TO CURRENT USER *************
			
			List<UserAndPricePOJO> priceThatOtherWillPay = new ArrayList<>();

			String queryString2 = " SELECT new com.expenses.pojo.UserAndPricePOJO(e.contriUserUuid as userUuid, e.contriUserName as userName, sum(e.price) as totalPriceToPay) "
					+ " FROM ProductEntity a LEFT JOIN ExpensesForEachEntity e ON a.productSrno = e.productSrno "
					+ " WHERE a.finalStatus != 2 AND a.buyerUserUuid = '" + dailyExpensesReqVO.getBuyerUserUuid() + "' " + whereCondition 
					+ " GROUP BY e.contriUserUuid, e.contriUserName";
			
			Query query2 = entityManager.createQuery(queryString2, UserAndPricePOJO.class);
			
			priceThatOtherWillPay = query2.getResultList();
			
			dailyExpensesRespVO.setPriceThatOtherWillPay(priceThatOtherWillPay);
			
			entityManager.clear();
			
			
			
			// ********* FETCHING DETAILS THAT HOW MUCH CURRENT USER WILL PAY TO OTHER USERS *************

			List<UserAndPricePOJO> priceThatWillBePaidToOthers = new ArrayList<>();

			String queryString3 = " SELECT new com.expenses.pojo.UserAndPricePOJO(e.buyerUserUuid as userUuid, e.buyerName as userName, sum(e.price) as totalPriceToPay) "
					+ " FROM ProductEntity a LEFT JOIN ExpensesForEachEntity e ON a.productSrno = e.productSrno "
					+ " WHERE a.finalStatus != 2 AND a.buyerUserUuid != '" + dailyExpensesReqVO.getBuyerUserUuid() + "' "
							+ " AND e.contriUserUuid = '" + dailyExpensesReqVO.getBuyerUserUuid() + "' " + whereCondition 
					+ " GROUP BY e.buyerUserUuid, e.buyerName";
			
			Query query3 = entityManager.createQuery(queryString3, UserAndPricePOJO.class);
			
			priceThatWillBePaidToOthers = query3.getResultList();
			
			dailyExpensesRespVO.setPriceThatWillBePaidToOthers(priceThatWillBePaidToOthers);
			
			entityManager.clear();
			*/
			
			
			dailyExpensesRespVO.setResultVO(new ResultVO("200", "Details Fetched Successfully !!!", true));
			return dailyExpensesRespVO;
		}
		catch(Exception e) {
			e.printStackTrace();
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Something Went Wrong !!!", true));
			return dailyExpensesRespVO;
		}
	}



	@Override
	public DailyExpensesRespVO priceTogive(DailyExpensesReqVO dailyExpensesReqVO) {
		DailyExpensesRespVO dailyExpensesRespVO = new DailyExpensesRespVO();
		
		if (dailyExpensesReqVO == null) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		if (dailyExpensesReqVO.getBuyerUserUuid() == null) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		
		List<ProductDetailsVO> productDetailsVOs = new ArrayList<ProductDetailsVO>();
		String whereCondition = "";
				
		try {
			
			// If from date is requested then add condition for that.....
			if (dailyExpensesReqVO.getFromDate() != null) {
				whereCondition += (" AND a.buyDate >=" + dailyExpensesReqVO.getBuyDate());
			}
			// If to date is requested then add condition for that.....
			if (dailyExpensesReqVO.getToDate() != null) {
				whereCondition += (" AND a.buyDate <=" + dailyExpensesReqVO.getToDate());
			}
			
			// *********** ALL PRODUCT LIST **************
			// creating query dynamically to fetch all product details list between mentioned dates....
//			String queryString = " SELECT new com.expenses.pojo.ProductDetailsVO(a.productSrno, a.buyerUserUuid, a.buyerName, a.buyDate, a.product, a.productPrice) "
//					+ " FROM ProductEntity a WHERE a.finalStatus != 2 AND a.buyerUserUuid = '" + dailyExpensesReqVO.getBuyerUserUuid() + "' " + whereCondition;
//			
//			Query query = entityManager.createQuery(queryString, ProductDetailsVO.class);
//			
//			productDetailsVOs = query.getResultList();
//						
//			// saving all product details list to response POJO.....
//			dailyExpensesRespVO.setProductDetailsVOs(productDetailsVOs);
//			
//			entityManager.clear();
			
			
			// ********* FETCHING DETAILS THAT HOW MUCH OTHER USERS WILL PAY TO CURRENT USER *************
			
			List<UserAndPricePOJO> priceThatOtherWillPay = new ArrayList<>();

			String queryString2 = " SELECT new com.expenses.pojo.UserAndPricePOJO(e.contriUserUuid as userUuid, e.contriUserName as userName, sum(e.price) as totalPriceToPay) "
					+ " FROM ProductEntity a LEFT JOIN ExpensesForEachEntity e ON a.productSrno = e.productSrno "
					+ " WHERE a.finalStatus != 2 AND a.buyerUserUuid = '" + dailyExpensesReqVO.getBuyerUserUuid() + "' " + whereCondition 
					+ " GROUP BY e.contriUserUuid, e.contriUserName";
			
			Query query2 = entityManager.createQuery(queryString2, UserAndPricePOJO.class);
			
			priceThatOtherWillPay = query2.getResultList();
			
			dailyExpensesRespVO.setPriceThatOtherWillPay(priceThatOtherWillPay);
			
			entityManager.clear();
			
			
			
			// ********* FETCHING DETAILS THAT HOW MUCH CURRENT USER WILL PAY TO OTHER USERS *************

			List<UserAndPricePOJO> priceThatWillBePaidToOthers = new ArrayList<>();

			String queryString3 = " SELECT new com.expenses.pojo.UserAndPricePOJO(e.buyerUserUuid as userUuid, e.buyerName as userName, sum(e.price) as totalPriceToPay) "
					+ " FROM ProductEntity a LEFT JOIN ExpensesForEachEntity e ON a.productSrno = e.productSrno "
					+ " WHERE a.finalStatus != 2 AND a.buyerUserUuid != '" + dailyExpensesReqVO.getBuyerUserUuid() + "' "
							+ " AND e.contriUserUuid = '" + dailyExpensesReqVO.getBuyerUserUuid() + "' " + whereCondition 
					+ " GROUP BY e.buyerUserUuid, e.buyerName";
			
			Query query3 = entityManager.createQuery(queryString3, UserAndPricePOJO.class);
			
			priceThatWillBePaidToOthers = query3.getResultList();
			
			dailyExpensesRespVO.setPriceThatWillBePaidToOthers(priceThatWillBePaidToOthers);
			
			entityManager.clear();
			
			
			// Fetching the final price......
			List<UserAndPricePOJO> finalCalculations = new ArrayList<>();
			
			if (dailyExpensesReqVO.getMode() != null && dailyExpensesReqVO.getMode().equalsIgnoreCase("GET") && 
					(priceThatOtherWillPay != null && priceThatOtherWillPay.size() > 0) &&
					(priceThatWillBePaidToOthers == null || priceThatWillBePaidToOthers.size() == 0)) {
				// If all the products is brought by current user then the tab for pay to others will not be visible....
				dailyExpensesRespVO.setFinalCalculations(priceThatOtherWillPay);
			}
			else if (dailyExpensesReqVO.getMode() != null && (!dailyExpensesReqVO.getMode().equalsIgnoreCase("GET")) &&
					(priceThatWillBePaidToOthers != null && priceThatWillBePaidToOthers.size() > 0) && 
					(priceThatOtherWillPay == null || priceThatOtherWillPay.size() == 0)) {
				dailyExpensesRespVO.setFinalCalculations(priceThatWillBePaidToOthers);
			}
			else {				
				for (UserAndPricePOJO pricePOJO : priceThatOtherWillPay) {	// 
					for (UserAndPricePOJO pricePOJO2 : priceThatWillBePaidToOthers) {
						if (pricePOJO.getUserUuid().equalsIgnoreCase(pricePOJO2.getUserUuid())) {
							if (pricePOJO.getTotalPriceToPay() < pricePOJO2.getTotalPriceToPay()) {
								pricePOJO.setTotalPriceToPay(pricePOJO2.getTotalPriceToPay() - pricePOJO.getTotalPriceToPay());
								finalCalculations.add(pricePOJO);
							}
						}
					}
				}
			}
			
			if (finalCalculations != null && finalCalculations.size() > 0) {
				dailyExpensesRespVO.setFinalCalculations(finalCalculations);
			}
			
			dailyExpensesRespVO.setResultVO(new ResultVO("200", "Details Fetched Successfully !!!", true));
			return dailyExpensesRespVO;
		}
		catch(Exception e) {
			e.printStackTrace();
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Something Went Wrong !!!", true));
			return dailyExpensesRespVO;
		}
	}



	@Override
	public DailyExpensesRespVO priceToget(DailyExpensesReqVO dailyExpensesReqVO) {
		DailyExpensesRespVO dailyExpensesRespVO = new DailyExpensesRespVO();
		
		if (dailyExpensesReqVO == null) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		if (dailyExpensesReqVO.getBuyerUserUuid() == null) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		
		List<ProductDetailsVO> productDetailsVOs = new ArrayList<ProductDetailsVO>();
		String whereCondition = "";
				
		try {
			
			// If from date is requested then add condition for that.....
			if (dailyExpensesReqVO.getFromDate() != null) {
				whereCondition += (" AND a.buyDate >=" + dailyExpensesReqVO.getBuyDate());
			}
			// If to date is requested then add condition for that.....
			if (dailyExpensesReqVO.getToDate() != null) {
				whereCondition += (" AND a.buyDate <=" + dailyExpensesReqVO.getToDate());
			}
			
			// *********** ALL PRODUCT LIST **************
			// creating query dynamically to fetch all product details list between mentioned dates....
//			String queryString = " SELECT new com.expenses.pojo.ProductDetailsVO(a.productSrno, a.buyerUserUuid, a.buyerName, a.buyDate, a.product, a.productPrice) "
//					+ " FROM ProductEntity a WHERE a.finalStatus != 2 AND a.buyerUserUuid = '" + dailyExpensesReqVO.getBuyerUserUuid() + "' " + whereCondition;
//			
//			Query query = entityManager.createQuery(queryString, ProductDetailsVO.class);
//			
//			productDetailsVOs = query.getResultList();
//						
//			// saving all product details list to response POJO.....
//			dailyExpensesRespVO.setProductDetailsVOs(productDetailsVOs);
//			
//			entityManager.clear();
			
			
			// ********* FETCHING DETAILS THAT HOW MUCH OTHER USERS WILL PAY TO CURRENT USER *************
			
			List<UserAndPricePOJO> priceThatOtherWillPay = new ArrayList<>();

			String queryString2 = " SELECT new com.expenses.pojo.UserAndPricePOJO(e.contriUserUuid as userUuid, e.contriUserName as userName, sum(e.price) as totalPriceToPay) "
					+ " FROM ProductEntity a LEFT JOIN ExpensesForEachEntity e ON a.productSrno = e.productSrno "
					+ " WHERE a.finalStatus != 2 AND a.buyerUserUuid = '" + dailyExpensesReqVO.getBuyerUserUuid() + "' " + whereCondition 
					+ " GROUP BY e.contriUserUuid, e.contriUserName";
			
			Query query2 = entityManager.createQuery(queryString2, UserAndPricePOJO.class);
			
			priceThatOtherWillPay = query2.getResultList();
			
			dailyExpensesRespVO.setPriceThatOtherWillPay(priceThatOtherWillPay);
			
			entityManager.clear();
			
			
			
			// ********* FETCHING DETAILS THAT HOW MUCH CURRENT USER WILL PAY TO OTHER USERS *************

			List<UserAndPricePOJO> priceThatWillBePaidToOthers = new ArrayList<>();

			String queryString3 = " SELECT new com.expenses.pojo.UserAndPricePOJO(e.buyerUserUuid as userUuid, e.buyerName as userName, sum(e.price) as totalPriceToPay) "
					+ " FROM ProductEntity a LEFT JOIN ExpensesForEachEntity e ON a.productSrno = e.productSrno "
					+ " WHERE a.finalStatus != 2 AND a.buyerUserUuid != '" + dailyExpensesReqVO.getBuyerUserUuid() + "' "
							+ " AND e.contriUserUuid = '" + dailyExpensesReqVO.getBuyerUserUuid() + "' " + whereCondition 
					+ " GROUP BY e.buyerUserUuid, e.buyerName";
			
			Query query3 = entityManager.createQuery(queryString3, UserAndPricePOJO.class);
			
			priceThatWillBePaidToOthers = query3.getResultList();
			
			dailyExpensesRespVO.setPriceThatWillBePaidToOthers(priceThatWillBePaidToOthers);
			
			entityManager.clear();
			
			
			// Fetching the final price......
			List<UserAndPricePOJO> finalCalculations = new ArrayList<>();
			
			if (dailyExpensesReqVO.getMode() != null && dailyExpensesReqVO.getMode().equalsIgnoreCase("GET") && 
					(priceThatOtherWillPay != null && priceThatOtherWillPay.size() > 0) &&
					(priceThatWillBePaidToOthers == null || priceThatWillBePaidToOthers.size() == 0)) {
				// If all the products is brought by current user then the tab for pay to others will not be visible....
				dailyExpensesRespVO.setFinalCalculations(priceThatOtherWillPay);
			}
			else if (dailyExpensesReqVO.getMode() != null && (!dailyExpensesReqVO.getMode().equalsIgnoreCase("GET")) &&
					(priceThatWillBePaidToOthers != null && priceThatWillBePaidToOthers.size() > 0) && 
					(priceThatOtherWillPay == null || priceThatOtherWillPay.size() == 0)) {
				dailyExpensesRespVO.setFinalCalculations(priceThatWillBePaidToOthers);
			}
			else {				
				for (UserAndPricePOJO pricePOJO : priceThatOtherWillPay) {	// 
					for (UserAndPricePOJO pricePOJO2 : priceThatWillBePaidToOthers) {
						if (pricePOJO.getUserUuid().equalsIgnoreCase(pricePOJO2.getUserUuid())) {
							if (pricePOJO.getTotalPriceToPay() > pricePOJO2.getTotalPriceToPay()) {
								pricePOJO.setTotalPriceToPay(pricePOJO.getTotalPriceToPay() - pricePOJO2.getTotalPriceToPay());
								finalCalculations.add(pricePOJO);
							}							
						}
					}
				}
			}
			
			if (finalCalculations != null && finalCalculations.size() > 0) {
				dailyExpensesRespVO.setFinalCalculations(finalCalculations);
			}
			
			dailyExpensesRespVO.setResultVO(new ResultVO("200", "Details Fetched Successfully !!!", true));
			return dailyExpensesRespVO;
		}
		catch(Exception e) {
			e.printStackTrace();
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Something Went Wrong !!!", true));
			return dailyExpensesRespVO;
		}
	}



	@Override
	public DailyExpensesRespVO finalResult(DailyExpensesReqVO dailyExpensesReqVO) {
		DailyExpensesRespVO dailyExpensesRespVO = new DailyExpensesRespVO();
		
		if (dailyExpensesReqVO == null) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		if (dailyExpensesReqVO.getBuyerUserUuid() == null) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		
		List<ProductDetailsVO> productDetailsVOs = new ArrayList<ProductDetailsVO>();
		String whereCondition = "";
				
		try {
			
			// If from date is requested then add condition for that.....
			if (dailyExpensesReqVO.getFromDate() != null) {
				whereCondition += (" AND a.buyDate >=" + dailyExpensesReqVO.getBuyDate());
			}
			// If to date is requested then add condition for that.....
			if (dailyExpensesReqVO.getToDate() != null) {
				whereCondition += (" AND a.buyDate <=" + dailyExpensesReqVO.getToDate());
			}
			
			// *********** ALL PRODUCT LIST **************
			// creating query dynamically to fetch all product details list between mentioned dates....
//			String queryString = " SELECT new com.expenses.pojo.ProductDetailsVO(a.productSrno, a.buyerUserUuid, a.buyerName, a.buyDate, a.product, a.productPrice) "
//					+ " FROM ProductEntity a WHERE a.finalStatus != 2 AND a.buyerUserUuid = '" + dailyExpensesReqVO.getBuyerUserUuid() + "' " + whereCondition;
//			
//			Query query = entityManager.createQuery(queryString, ProductDetailsVO.class);
//			
//			productDetailsVOs = query.getResultList();
//						
//			// saving all product details list to response POJO.....
//			dailyExpensesRespVO.setProductDetailsVOs(productDetailsVOs);
//			
//			entityManager.clear();
			
			
			// ********* FETCHING DETAILS THAT HOW MUCH OTHER USERS WILL PAY TO CURRENT USER *************
			
			List<UserAndPricePOJO> priceThatOtherWillPay = new ArrayList<>();

			String queryString2 = " SELECT new com.expenses.pojo.UserAndPricePOJO(e.contriUserUuid as userUuid, e.contriUserName as userName, sum(e.price) as totalPriceToPay) "
					+ " FROM ProductEntity a LEFT JOIN ExpensesForEachEntity e ON a.productSrno = e.productSrno "
					+ " WHERE a.finalStatus != 2 AND a.buyerUserUuid = '" + dailyExpensesReqVO.getBuyerUserUuid() + "' " + whereCondition 
					+ " GROUP BY e.contriUserUuid, e.contriUserName";
			
			Query query2 = entityManager.createQuery(queryString2, UserAndPricePOJO.class);
			
			priceThatOtherWillPay = query2.getResultList();
			
			dailyExpensesRespVO.setPriceThatOtherWillPay(priceThatOtherWillPay);
			
			entityManager.clear();
			
			
			
			// ********* FETCHING DETAILS THAT HOW MUCH CURRENT USER WILL PAY TO OTHER USERS *************

			List<UserAndPricePOJO> priceThatWillBePaidToOthers = new ArrayList<>();

			String queryString3 = " SELECT new com.expenses.pojo.UserAndPricePOJO(e.buyerUserUuid as userUuid, e.buyerName as userName, sum(e.price) as totalPriceToPay) "
					+ " FROM ProductEntity a LEFT JOIN ExpensesForEachEntity e ON a.productSrno = e.productSrno "
					+ " WHERE a.finalStatus != 2 AND a.buyerUserUuid != '" + dailyExpensesReqVO.getBuyerUserUuid() + "' "
							+ " AND e.contriUserUuid = '" + dailyExpensesReqVO.getBuyerUserUuid() + "' " + whereCondition 
					+ " GROUP BY e.buyerUserUuid, e.buyerName";
			
			Query query3 = entityManager.createQuery(queryString3, UserAndPricePOJO.class);
			
			priceThatWillBePaidToOthers = query3.getResultList();
			
			dailyExpensesRespVO.setPriceThatWillBePaidToOthers(priceThatWillBePaidToOthers);
			
			entityManager.clear();
			
			
			// Fetching the final price......
			List<UserAndPricePOJO> finalCalculations = new ArrayList<>();
			
			if (dailyExpensesReqVO.getMode() != null && dailyExpensesReqVO.getMode().equalsIgnoreCase("GET") && 
					(priceThatOtherWillPay != null && priceThatOtherWillPay.size() > 0) &&
					(priceThatWillBePaidToOthers == null || priceThatWillBePaidToOthers.size() == 0)) {
				// If all the products is brought by current user then the tab for pay to others will not be visible....
				dailyExpensesRespVO.setFinalCalculations(priceThatOtherWillPay);
			}
			else if (dailyExpensesReqVO.getMode() != null && (!dailyExpensesReqVO.getMode().equalsIgnoreCase("GET")) &&
					(priceThatWillBePaidToOthers != null && priceThatWillBePaidToOthers.size() > 0) && 
					(priceThatOtherWillPay == null || priceThatOtherWillPay.size() == 0)) {
				dailyExpensesRespVO.setFinalCalculations(priceThatWillBePaidToOthers);
			}
			else {				
				boolean isSameUser = false;
				if (dailyExpensesReqVO.getMode() != null && dailyExpensesReqVO.getMode().equalsIgnoreCase("GET")) {
					for (UserAndPricePOJO pricePOJO : priceThatOtherWillPay) {	// list of price that other will pay....
						isSameUser = false;
						for (UserAndPricePOJO pricePOJO2 : priceThatWillBePaidToOthers) {	// list of price that current user will pay to others....
							if (pricePOJO.getUserUuid().equalsIgnoreCase(pricePOJO2.getUserUuid())) {
								if (pricePOJO.getTotalPriceToPay() > pricePOJO2.getTotalPriceToPay()) {
									pricePOJO.setTotalPriceToPay(pricePOJO.getTotalPriceToPay() - pricePOJO2.getTotalPriceToPay());										
									finalCalculations.add(pricePOJO);
								}
								isSameUser = true;
								break;
							}
						}
						if (!isSameUser) {							
							finalCalculations.add(pricePOJO);
						}
					}
				}
				else {
					for (UserAndPricePOJO pricePOJO : priceThatWillBePaidToOthers) {	// list of price that current user will pay to others....
						isSameUser = false;
						for (UserAndPricePOJO pricePOJO2 : priceThatOtherWillPay) {	// list of price that other will pay....
							if (pricePOJO.getUserUuid().equalsIgnoreCase(pricePOJO2.getUserUuid())) {
								if (pricePOJO.getTotalPriceToPay() > pricePOJO2.getTotalPriceToPay()) {
									pricePOJO.setTotalPriceToPay(pricePOJO.getTotalPriceToPay() - pricePOJO2.getTotalPriceToPay());										
									finalCalculations.add(pricePOJO);
								}
								isSameUser = true;
								break;
							}
						}
						if (!isSameUser) {							
							finalCalculations.add(pricePOJO);
						}
					}
				}
				
			}
			
			if (finalCalculations != null && finalCalculations.size() > 0) {
				dailyExpensesRespVO.setFinalCalculations(finalCalculations);
			}
			
			dailyExpensesRespVO.setResultVO(new ResultVO("200", "Details Fetched Successfully !!!", true));
			return dailyExpensesRespVO;
		}
		catch(Exception e) {
			e.printStackTrace();
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Something Went Wrong !!!", true));
			return dailyExpensesRespVO;
		}
	}



	@Override
	public DailyExpensesRespVO getAllActiveUsers(DailyExpensesReqVO dailyExpensesReqVO) {
		DailyExpensesRespVO dailyExpensesRespVO = new DailyExpensesRespVO();
		
//		if (dailyExpensesReqVO == null) {
//			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Imroper Data !!!", true));
//			return dailyExpensesRespVO;
//		}
//		if (dailyExpensesReqVO.getBuyerUserUuid() == null) {
//			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Imroper Data !!!", true));
//			return dailyExpensesRespVO;
//		}
		
		List<ProductDetailsVO> productDetailsVOs = new ArrayList<ProductDetailsVO>();
		String whereCondition = "";
				
		try {
			
			List<UserAndPricePOJO> activeUsersList = new ArrayList<>();
			
			List<UserEntity> userEntities = userRepo.getAllActiveUsers();
			
			if (userEntities != null && userEntities.size() > 0) {
				for (UserEntity userEntity : userEntities) {
					UserAndPricePOJO userDetail = new UserAndPricePOJO();
					userDetail.setUserName(userEntity.getName());
					userDetail.setUserUuid(userEntity.getUserUuid());
					userDetail.setUserEmail(userEntity.getEmail());

					activeUsersList.add(userDetail);
				}
			}
			
			if (activeUsersList != null && activeUsersList.size() > 0) {
				dailyExpensesRespVO.setUsersList(activeUsersList);
			}
			dailyExpensesRespVO.setResultVO(new ResultVO("200", "Details Fetched Successfully !!!", true));
			return dailyExpensesRespVO;
		}
		catch(Exception e) {
			e.printStackTrace();
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Something Went Wrong !!!", true));
			return dailyExpensesRespVO;
		}
	}



	@Override
	public DailyExpensesRespVO getAllOtherActiveUsers(DailyExpensesReqVO dailyExpensesReqVO) {
		DailyExpensesRespVO dailyExpensesRespVO = new DailyExpensesRespVO();
		
		if (dailyExpensesReqVO == null) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		if (dailyExpensesReqVO.getBuyerUserUuid() == null || dailyExpensesReqVO.getBuyerUserUuid().isBlank()) {
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Imroper Data !!!", true));
			return dailyExpensesRespVO;
		}
		
		List<ProductDetailsVO> productDetailsVOs = new ArrayList<ProductDetailsVO>();
		String whereCondition = "";
				
		try {
			
			List<UserAndPricePOJO> activeUsersList = new ArrayList<>();
			
			List<UserEntity> userEntities = userRepo.getAllOtherActiveUsers(dailyExpensesReqVO.getBuyerUserUuid());
			
			if (userEntities != null && userEntities.size() > 0) {
				for (UserEntity userEntity : userEntities) {
					UserAndPricePOJO userDetail = new UserAndPricePOJO();
					userDetail.setUserName(userEntity.getName());
					userDetail.setUserUuid(userEntity.getUserUuid());
					userDetail.setUserEmail(userEntity.getEmail());

					activeUsersList.add(userDetail);
				}
			}
			
			if (activeUsersList != null && activeUsersList.size() > 0) {
				dailyExpensesRespVO.setUsersList(activeUsersList);
			}
			dailyExpensesRespVO.setResultVO(new ResultVO("200", "Details Fetched Successfully !!!", true));
			return dailyExpensesRespVO;
		}
		catch(Exception e) {
			e.printStackTrace();
			dailyExpensesRespVO.setResultVO(new ResultVO("500", "Something Went Wrong !!!", true));
			return dailyExpensesRespVO;
		}
	}

}
