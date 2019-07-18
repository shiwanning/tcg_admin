package com.tcg.admin.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
import com.tcg.admin.to.PageableTO;
import com.yx.commons.utils.DateUtils;

@RestController
@RequestMapping(value = "/resources/randy/go", produces = MediaType.APPLICATION_JSON_VALUE)
public class RandyResource {
		
	private Map<String, Object> m = Maps.newHashMap();
	
	@PersistenceContext
    private EntityManager entityManager;
	
	private String sql = "SELECT    f2.\"用户名\", f2.\"以前系统 customerId\", f2.\"目前系统 customerId\", f2.\"最后成功提现金额\", f2.\"最后提现成功时间\",\r\n" + 
			"          f2.\"用户类型\", f2.\"目前可用余额\", \r\n" + 
			"                                                  f2.\"目前余额\", \r\n" + 
			"                                                  f2.\"ACS最后帐变余额\", \r\n" + 
			"                                                  f2.\"ACS最后帐变可用余额\", \r\n" + 
			"                                                  f2.\"ACS最后帐变时间\",\r\n" + 
			"                                                  f2.\"WPS最后余额\",\r\n" + 
			"                                                  f2.\"WPS最后更新时间\",\r\n" + 
			"                                                  f2.\"最后登入时间\",\r\n" + 
			"          Nvl(mbd.deposit_amount, '-1') \"最后充值金额\", \r\n" + 
			"          mbd.deposit_date \"最后充值时间\", \r\n" + 
			"          f2.account_id \"目前的accountId\", \r\n" +
			"          f2.account_update_time \"目前account最后异动时间\", \r\n" +
			"          f2.\"最后提现成功的申请时间\" \r\n" + 
			"FROM      ( \r\n" + 
			"                    SELECT    f1.*, \r\n" + 
			"                              Nvl(mbw.paid_amount, '-1') \"最后成功提现金额\", \r\n" + 
			"                              mbw.process_time \"最后提现成功时间\", \r\n" +
			"                              mbw.request_time \"最后提现成功的申请时间\" \r\n" +
			"                    FROM      ( \r\n" + 
			"                                        SELECT    xx.customer_name \"用户名\", \r\n" + 
			"                                                  xx.customer_id \"以前系统 customerId\", \r\n" + 
			"                                                  xx.current_customer_id \"目前系统 customerId\", \r\n" + 
			"                                                  CASE \r\n" + 
			"                                                            WHEN xx.current_customer_id > 51000000 THEN '新用户' \r\n" + 
			"                                                            ELSE '旧用户' \r\n" + 
			"                                                  END \"用户类型\", \r\n" + 
			"                                                  Nvl(ac.avail_balance, '-1') \"目前可用余额\", \r\n" + 
			"                                                  Nvl(ac.balance, '-1') \"目前余额\", \r\n" + 
			"                                                  alb.balance \"ACS最后帐变余额\", \r\n" + 
			"                                                  alb.avail_balance \"ACS最后帐变可用余额\", \r\n" + 
			"                                                  alb.time \"ACS最后帐变时间\",\r\n" + 
			"                                                  wb.MAIN_AVAIL_BALANCE \"WPS最后余额\",\r\n" + 
			"                                                  wb.UPDATE_TIME \"WPS最后更新时间\",\r\n" + 
			"                                                  xx.LAST_LOGIN_TIME \"最后登入时间\",\r\n" + 
			"                                                  ac.account_id, \r\n" + 
			"                                                  ac.update_time account_update_time \r\n " +
			"                                        FROM      ( \r\n" + 
			"                                                             SELECT     Nvl(wc.customer_id, uc.customer_id) customer_id,\r\n" + 
			"                                                                        Nvl(wc.new_customer_id, uc.customer_id) current_customer_id,\r\n" + 
			"                                                                        uc.customer_name,\r\n" + 
			"                                                                        cp.LAST_LOGIN_TIME\r\n" + 
			"                                                             FROM       tcg_core.us_customer uc \r\n" + 
			"                                                             INNER JOIN tcg_core.US_CUSTOMER_PROFILE cp ON cp.CUSTOMER_ID = uc.CUSTOMER_ID\r\n" + 
			"                                                             LEFT JOIN tcg_core.wps_customer wc\r\n" + 
			"                                                             ON         uc.customer_id = Nvl(wc.new_customer_id, wc.customer_id)\r\n" + 
			"                                                             WHERE      uc.customer_name = '%s@%s'\r\n" + 
			"                                                             ) xx\r\n" + 
			"                                        LEFT JOIN tcg_core.acs_account ac \r\n" + 
			"                                        ON        ac.account_type_id = 2 \r\n" + 
			"                                        AND       xx.current_customer_id = ac.customer_id \r\n" + 
			"                                        LEFT JOIN tcg_core.acs_latest_bal alb \r\n" + 
			"                                        ON        alb.acct_id = ac.account_id\r\n" + 
			"                                        LEFT JOIN tcg_core.WPS_BALANCE wb \r\n" + 
			"                                        ON        wb.CUSTOMER_ID = xx.customer_id\r\n" + 
			"                                         ) f1 \r\n" + 
			"                    LEFT JOIN tcg_mcsdb.mcs_bank_withdraw mbw \r\n" + 
			"                    ON        f1.\"目前系统 customerId\" = mbw.customer_id \r\n" + 
			"                    AND       withdraw_status = 'PAID' \r\n" + 
			"                    AND       process_time < date'2019-04-03' \r\n" + 
			"                    ORDER BY  process_time DESC nulls last FETCH FIRST 1 ROW ONLY  ) f2\r\n" + 
			"LEFT JOIN tcg_mcsdb.mcs_bank_deposit mbd \r\n" + 
			"ON        mbd.merchant_code \r\n" + 
			"                    || '@' \r\n" + 
			"                    || mbd.username = f2.\"用户名\" \r\n" + 
			"AND       deposit_date < date'2019-04-03' \r\n" + 
			"AND DEPOSIT_STATUS = 'A'\r\n" + 
			"ORDER BY  deposit_date DESC nulls last FETCH FIRST 1 ROW ONLY \r\n" + 
			"\r\n" + 
			"";
	
	
	private String lastAcsTransSql = "select att.amount , att.current_avail_balance "
			+ " from tcg_core.ACS_TRANSACTION att "
			+ " inner join tcg_core.ACS_ACCOUNT aaa ON att.account_id = aaa.account_id "
			+ " where aaa.account_id = %s "
			+ " and tx_time > date'2019-04-04' "
			+ " and aaa.account_type_id = 2 "
			+ " order by att.tx_time asc FETCH FIRST 1 ROW ONLY";
	
	private String withdrawSql = "SELECT paid_amount, to_char(request_time, 'yyyy-mm-dd hh24:mi:ss') FROM tcg_mcsdb.mcs_bank_withdraw \r\n" + 
			"WHERE customer_id = %s \r\n" + 
			"AND withdraw_status = 'PAID' \r\n" + 
			"AND process_time < date'2019-04-03' \r\n" + 
			"AND process_time > date'2019-03-04' \r\n" + 
			"ORDER BY  process_time DESC";
	
	private String depositSql = "SELECT DEPOSIT_AMOUNT, to_char(DEPOSIT_DATE, 'yyyy-mm-dd hh24:mi:ss') from tcg_mcsdb.mcs_bank_deposit mbd \r\n" + 
			"WHERE mbd.merchant_code || '@' || mbd.username = '%s' \r\n" + 
			"AND       deposit_date < date'2019-04-03' \r\n" + 
			"AND       deposit_date > date'2019-03-04' \r\n" + 
			"AND DEPOSIT_STATUS = 'A' \r\n" + 
			"ORDER BY  deposit_date desc ";
	
	@GetMapping("/gg2")
    public Map<String, Object> gogo(
    		@RequestParam(value = "customerId", required = true) Long customerId
                                 ) {
        PageableTO pageableTo = new PageableTO();
        String asql = "select to_number(account_id) from tcg_core.acs_account_temp where customer_Id = '" + customerId + "' and account_type_id = '2'";
        List x = findBySql(asql, m);
        Map<String, Object> mm = Maps.newLinkedHashMap();
        if(!x.isEmpty()) {
        	BigDecimal o = (BigDecimal) x.get(0);
        	mm.put("accountId", o);
        	mm.put("accountId来源", "acs_account_temp");
        } else {
        	mm.put("accountId", null);
        }
        
        if(mm.get("accountId") != null) {
        	return mm;
        }
        
        String tsql = "select account_id from tcg_core.acs_account_prod where customer_Id = " + customerId + " and account_type_id = 2";
        System.out.println(tsql);
        List xt = findBySql(tsql, m);
        if(!xt.isEmpty()) {
        	BigDecimal o = (BigDecimal) xt.get(0);
        	mm.put("accountId", o);
        	mm.put("accountId来源", "acs_account_prod");
        } else {
        	mm.put("accountId", null);
        }
        return mm;
        
    }
	
	@GetMapping()
    public Map<String, Object> gogo(
    		@RequestParam(value = "merchantCode", required = true) String merchantCode,
            @RequestParam(value = "customerName", required = false) String customerName
                                 ) {
        PageableTO pageableTo = new PageableTO();
        String realSql = String.format(sql, merchantCode, customerName);
        List x = findBySql(realSql, m);
        Map<String, Object> mm = Maps.newLinkedHashMap();
        if(!x.isEmpty()) {
        	Object[] o = (Object[]) x.get(0);
        	mm.put("用户名", (o[0] instanceof Date) ? DateFormatUtils.format((Date)o[0], "yyyy-MM-dd HH:mm:ss") : o[0]);
        	mm.put("以前系统 customerId", o[1]);
        	mm.put("目前系统 customerId", o[2]);
        	mm.put("最后成功提现金额", o[3]);
        	mm.put("最后提现成功时间", (o[4] instanceof Date) ? DateFormatUtils.format((Date)o[4], "yyyy-MM-dd HH:mm:ss") : o[4]);
        	mm.put("最后提现成功的申请时间", (o[18] instanceof Date) ? DateFormatUtils.format((Date)o[18], "yyyy-MM-dd HH:mm:ss") : o[18]);
        	mm.put("用户类型", o[5]);
        	mm.put("目前可用余额", o[6]);
        	mm.put("目前余额", o[7]);
        	mm.put("WPS最后余额时间", (o[11] instanceof Date) ? DateFormatUtils.format((Date)o[11], "yyyy-MM-dd HH:mm:ss") : o[11]);
        	mm.put("WPS最后更新时间", (o[12] instanceof Date) ? DateFormatUtils.format((Date)o[12], "yyyy-MM-dd HH:mm:ss") : o[12]);
        	mm.put("最后登入时间", (o[13] instanceof Date) ? DateFormatUtils.format((Date)o[13], "yyyy-MM-dd HH:mm:ss") : o[13]);
        	mm.put("最后充值金额", o[14]);
        	mm.put("最后充值时间", (o[15] instanceof Date) ? DateFormatUtils.format((Date)o[15], "yyyy-MM-dd HH:mm:ss") : o[15]);
        	mm.put("目前的 accountId", o[16]);
        	mm.put("目前account最后异动时间", (o[17] instanceof Date) ? DateFormatUtils.format((Date)o[17], "yyyy-MM-dd HH:mm:ss") : o[17]);
        	
        	
        	if(Longs.tryParse(o[16] == null ? new Object().toString() : o[16].toString()) != null) {
        		Long accountId = Longs.tryParse(o[16].toString());
        		
        		if(o[17] != null) {
        			Date acsUpdateTime = (Date) o[17];
        			// comment: 如果帐变在 4-4 之后，需要取得 4/4 开战余额
        			if(acsUpdateTime.after(DateUtils.parse("2019-04-04", "yyyy-MM-dd"))) {
        				List acsTransactions = findBySql(String.format(lastAcsTransSql, accountId), m);
        				if(!acsTransactions.isEmpty()) {
        					Object[] firstTransactional = (Object[]) acsTransactions.get(0);
        					mm.put("开站可用余额", ((BigDecimal) firstTransactional[1]).subtract((BigDecimal) firstTransactional[0]));
        					mm.put("开站后首次交易金额", firstTransactional[0]);
        					mm.put("开站后首次交易后金额", firstTransactional[1]);
        				}
        				
        			} else {
        				mm.put("开站可用余额", o[6]);
        			}
        			
        			
        			
        		}
        	}
        	mm.put("3月4号之后提现明细", getWithdrawDetail(Integer.parseInt(o[2].toString())));
        	mm.put("3月4号之后充值明细", getdepositDetail(merchantCode + "@" + customerName));
        	
        } else {
        	mm.put("結果", "什么都找不到");
        }
        
        return mm;
    }
	
	
	private Object getdepositDetail(String customerName) {
		String sql = String.format(depositSql, customerName);
		List<Object[]> x = findBySql(sql, m);
		List<Map<String, Object>> details = new LinkedList<>();
		for(Object[] row : x) {
			Map<String, Object> detail = new HashMap<String, Object>();
			BigDecimal amount = (BigDecimal) row[0];
			String date = (String) row[1];
			detail.put("金额", amount);
			detail.put("时间", date);
			details.add(detail);
		}
		return details;
	}

	private Object getWithdrawDetail(Integer customerId) {
		String sql = String.format(withdrawSql, customerId);
		List<Object[]> x = findBySql(sql, m);
		List<Map<String, Object>> details = new LinkedList<>();
		for(Object[] row : x) {
			Map<String, Object> detail = new HashMap<String, Object>();
			BigDecimal amount = (BigDecimal) row[0];
			String date = (String) row[1];
			detail.put("金额", amount);
			detail.put("时间", date);
			details.add(detail);
		}
		return details;
	}

	public List findBySql(final String sql, final Map<String, Object> parameterMap){
        return getListForPage4SQL(sql, parameterMap, null, null);
    }
	
	public List getListForPage4SQL(final String sql, final Map<String, Object> parameterMap, final Integer offset,  final Integer length) {
        return this.getResult(entityManager.createNativeQuery(sql), parameterMap, offset, length);
    }

    private List getResult(Query query, final Map<String, Object> parameterMap, final Integer offset,  final Integer length){
        if (!parameterMap.isEmpty()) {
            for (Entry<String, Object> entry : parameterMap.entrySet()) {
            	String key = entry.getKey();
                if (parameterMap.get(key) instanceof List)
                    query.setParameter(key, (List)parameterMap.get(key));
                else
                    query.setParameter(key, parameterMap.get(key));
            }
        }
        if(offset != null && length != null){
            query.setFirstResult(offset);
            query.setMaxResults(length);
        }
        return query.getResultList();
    }
}
