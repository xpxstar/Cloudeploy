${variableName} = {
	"${resourceName}" => {
		${params?default('')}
	}
}
if !defined(${capitalizedResourceType}["${resourceName}"]){
	create_resources('${resourceType}', ${variableName})
	<#if isClassType>
	contain ${resourceName}
	</#if>
}