/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.service.service.item;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.service.dao.item.SerGasqSortDao;
import com.thinkgem.jeesite.modules.service.dao.item.SerGasqTagsDao;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemCommodityDao;
import com.thinkgem.jeesite.modules.service.dao.item.SerItemInfoDao;
import com.thinkgem.jeesite.modules.service.entity.item.*;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.entity.Menu;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import javafx.collections.transformation.SortedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类Service
 * @author a
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class SerGasqSortService extends CrudService<SerGasqSortDao, SerGasqSort> {

	@Autowired
	SerGasqSortDao serGasqSortDao;
	@Autowired
	SerGasqTagsDao serGasqTagsDao;

	public List<SortTree> getSortList() {
		SerGasqSort serGasqSort=new SerGasqSort();
		serGasqSort.setLevel(1); //一级分类
		List<SerGasqSort> list1 = serGasqSortDao.findList(serGasqSort);
		serGasqSort.setLevel(2);//二级分类
		List<SerGasqSort> list2 = serGasqSortDao.findList(serGasqSort);
		serGasqSort.setLevel(3);	//三级
		List<SerGasqSort> list3 = serGasqSortDao.findList(serGasqSort);

		SerGasqTags serGasqTags=new SerGasqTags();
		List<SerGasqTags> tagsList = serGasqTagsDao.findList(serGasqTags);
		//一级
		List<SortTree> sortTree1=new ArrayList<SortTree>();
		if (null != list1){
			for (SerGasqSort sort1:list1)	{
				SortTree first=new SortTree();
				first.setLabel(sort1.getName());
				first.setValue(sort1.getId());

				List<SortTree> sortTrees2=new ArrayList<SortTree>();
				//二级
				if (null != list2){
					for (SerGasqSort sort2:list2){
						if (sort1.getId().equals(sort2.getPid())){
							SortTree two=new SortTree();
							two.setLabel(sort2.getName());
							two.setValue(sort2.getId());

							//三级
							List<SortTree> sortTrees3 = new ArrayList<SortTree>();
							if (null != list3){
								for (SerGasqSort sort3:list3){
									if (sort2.getId().equals(sort3.getPid())){
										SortTree three=new SortTree();
										three.setLabel(sort3.getName());
										three.setValue(sort3.getId());

										//四级
										List<SortTree> sortTrees4 = new ArrayList<SortTree>();

										if (null != tagsList){
											for (SerGasqTags tags:tagsList){
												if (sort3.getId().equals(tags.getSortId())){
													SortTree four=new SortTree();
													four.setLabel(tags.getTagName());
													four.setValue(tags.getId());
													System.out.println(four+"************************");
													sortTrees4.add(four);
												}

											}
										}
										three.setChildren(sortTrees4);
										sortTrees3.add(three);
									}
								}
							}
							two.setChildren(sortTrees3);
							sortTrees2.add(two);
						}
					}
				}
				first.setChildren(sortTrees2);
				sortTree1.add(first);
			}
		}
		return sortTree1;
	}
}