// 当前是否处于创建类的阶段
// 因为是全局变量，所以不支持并发调用
var jInitializing = false;

function jClass(baseClass, prop) {
	// 只接受一个参数的情况 - jClass(prop)
	if (typeof (baseClass) === "object") {
		prop = baseClass;
		baseClass = null;
	}
	// 本次调用所创建的类（构造函数）
	function Node() {
		// 如果当前处于实例化类的阶段，则调用init原型函数
		if (!jInitializing) {
			if (baseClass) {
				this.base = baseClass.prototype;
			}
			this.init.apply(this, arguments);
		}
	}

	// 如果此类需要从其它类扩展
	if (baseClass) {
		jInitializing = true;
		Node.prototype = new baseClass();
		Node.prototype.constructor = Node;
		jInitializing = false;
	}

	// 覆盖父类的同名函数
	for ( var name in prop) {
		if (prop.hasOwnProperty(name)) {
			Node.prototype[name] = prop[name];
		}
	}

	return Node;
};