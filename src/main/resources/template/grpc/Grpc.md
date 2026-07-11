# ${htmlEscape(desc)}

**URI:** ${uri}

**Service:** ${name}

**Protocol:** ${protocol}

**Author:** ${author}

**Version:** ${version}
<% for(doc in list){ %>
<%if(doc.deprecated){%>

## ~~${htmlEscape(doc.desc)}~~

<%}else{%>

## ${htmlEscape(doc.desc)}

<%}%>

**Definition:** ${doc.escapeMethodDefinition}

<%if(isNotEmpty(doc.author)){%>
**Author:** ${doc.author}
<%}%>

<%if(isNotEmpty(doc.detail)){%>
**Description:** ${doc.detail}
<%}%>

**MethodType:** ${doc.methodType}

<%if(isNotEmpty(doc.requestParams)){%>
**Invoke-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
<% for(param in doc.requestParams){ %>
|${param.field}|${htmlEscape(param.type)}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|
<%}%>
<%}%>

<%if(isNotEmpty(doc.responseParams)){%>
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
<% for(param in doc.responseParams){ %>
|${param.field}|${htmlEscape(param.type)}|${lineBreaksToBr(param.desc)}|${param.version}|
<%}%>
<%}%>

<%}%>