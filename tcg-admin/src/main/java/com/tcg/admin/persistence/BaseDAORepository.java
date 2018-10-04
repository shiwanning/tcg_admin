package com.tcg.admin.persistence;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;

@Repository
public class BaseDAORepository {

	private static final Integer DEFAULT_PAGE_NO = 1;
	private static final Integer DEFAULT_MAX_RESULT = 10;
	
    @PersistenceContext
    private EntityManager entityManager;
    /**
     * 分頁查詢 for SQL， 查詢條件內有「in」的情況，參數以Map存放
     * @param  sql
     * @param parameterMap 參數集合(Map)
     * @param offset 起始索引值
     * @param length 查詢筆數
     * @return
     */
    public List getListForPage(final String sql, final Map<String, Object> parameterMap, final Integer offset, final Integer length) {
        return this.getResult(entityManager.createQuery(sql), parameterMap, offset, length);
    }

    /**
     * 查詢 for HQL， 查詢條件內有「in」的情況，參數以Map存放
     * @param hql
     * @param parameterMap
     * @return List
     */
    public List findByHql(final String hql, final Map<String, Object> parameterMap){
        return getListForPage(hql, parameterMap, null, null);
    }


    /**
     * 查詢 for SQL， 查詢條件內有「in」的情況，參數以Map存放
     * @param sql
     * @param parameterMap
     * @return List
     */
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

    /**
     * @description : process the JPQL String to become a valid query the process
     * 
     * */
    public Map<String, Object> findObjectListByJpl(final String jpql, final Map<String, Object> parameterMap,final Integer pageNo,final  Integer maxResult){
    	/*return value container*/
    	Map<String, Object> map = Maps.newHashMap();

    	/*process the query string*/
		Query query = entityManager.createQuery(jpql); 
		
		/*set the paging of list
		 *If pageNo is zero ||  null  set to default value
		 *else pageNo - 1 because the index starts in zero then * to maxResult to achieve paging 
		 * */
		query.setFirstResult((pageNo != null && pageNo != 0) ? ((pageNo - 1) * maxResult) : ((DEFAULT_PAGE_NO - 1) * DEFAULT_PAGE_NO));
        query.setMaxResults((maxResult != null && maxResult != 0) ? maxResult : DEFAULT_MAX_RESULT);

        
        /*add the variables in conditions of string query*/
        if(MapUtils.isNotEmpty(parameterMap)){
        	
        	for (Entry<String, Object> entry : parameterMap.entrySet()) {
        		String key = entry.getKey();
        		if (parameterMap.get(key) instanceof List){
        			/*this is for list object in map*/
                    query.setParameter(key, (List) parameterMap.get(key));
        		}else{
        			/*this is for data types*/
                    query.setParameter(key, parameterMap.get(key));
        		}
        		
        	}
        }
        
        /*get the total count of list*/
        Integer totalCount = this.getTotalCount(jpql, parameterMap);
        
        /*get the total length of page to display, add 1 for even results*/
        Integer totalPage = (totalCount / query.getMaxResults()) + ((totalCount % query.getMaxResults()) == 0 ? 0:1);
        Integer newPageNo = pageNo;
        if (newPageNo > totalPage) {
            newPageNo = totalPage;
        }
        /*return variables*/
        map.put("getResults", query.getResultList());
        map.put("getTotalElements", totalCount);
        map.put("pageNo", newPageNo);
        map.put("maxResults", query.getMaxResults());
        map.put("getTotalPages", totalPage);

        return map;

    }
    
    public Integer getTotalCount(final String jpql,final Map<String, Object> parameterMap){
    	Query query = entityManager.createQuery(jpql);
    	
    	if(MapUtils.isNotEmpty(parameterMap)){
        	
        	for (Entry<String, Object> entry : parameterMap.entrySet()) {
        		String key = entry.getKey();
        		if (parameterMap.get(key) instanceof List){
                    query.setParameter(key, (List) parameterMap.get(key));
        		}else{
                    query.setParameter(key, parameterMap.get(key));
        		}
        		
        	}
        }
    	
		return query.getResultList().size();
    	
    }


}
