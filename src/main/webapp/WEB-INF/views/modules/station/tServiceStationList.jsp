<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>服务站管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/station/tServiceStation/">服务站列表</a></li>
		<shiro:hasPermission name="station:tServiceStation:edit"><li><a href="${ctx}/station/tServiceStation/form">服务站添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="tServiceStation" action="${ctx}/station/tServiceStation/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>服务站名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>服务站名称</th>
				<th>更新时间</th>
				<th>备注信息</th>
				<shiro:hasPermission name="station:tServiceStation:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="tServiceStation">
			<tr>
				<td><a href="${ctx}/station/tServiceStation/form?id=${tServiceStation.id}">
					${tServiceStation.name}
				</a></td>
				<td>
					<fmt:formatDate value="${tServiceStation.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${tServiceStation.remarks}
				</td>
				<shiro:hasPermission name="station:tServiceStation:edit"><td>
    				<a href="${ctx}/station/tServiceStation/form?id=${tServiceStation.id}">修改</a>
					<a href="${ctx}/station/tServiceStation/delete?id=${tServiceStation.id}" onclick="return confirmx('确认要删除该服务站吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>