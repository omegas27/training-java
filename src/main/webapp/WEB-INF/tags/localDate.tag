<%@ tag body-content="empty" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="value" required="true" type="java.time.LocalDate" %>
<%@ attribute name="pattern" required="false" type="java.lang.String" %>

<c:if test="${empty pattern}">
    <c:set var="pattern" value="yyyy-MM-dd"/>
</c:if>

<fmt:parseDate value="${value}" pattern="yyyy-MM-dd" var="parsedDate" type="date" parseLocale="en_GB"/>
<fmt:formatDate value="${parsedDate}" type="date" pattern="yyyy-MM-dd"/>