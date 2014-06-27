package org.ixming.inject4android;

/**
 * 配置需要使用动态注入的项。
 * <p>
 * 因为使用反射实现的缘故，当项目中的成员变量、方法等数量很多，而真正
 * 需要用到该框架的地方较少，请根据实际情况配置。
 * </p>
 * <p>
 * 默认情况下，全部支持。
 * </p>
 * @author Yin Yong
 * @version 1.0
 */
public class InjectConfigure {

	private static class SpecInjectConfigure extends InjectConfigure {
		
		SpecInjectConfigure setToAllInternal(boolean ifInject) {
			super.setToAll(ifInject);
			return this;
		}
		SpecInjectConfigure injectViewsInternal(boolean ifInject) {
			super.injectViews(ifInject);
			return this;
		}
		SpecInjectConfigure injectResesInternal(boolean ifInject) {
			super.injectReses(ifInject);
			return this;
		}
		SpecInjectConfigure injectOnClickMethodsInternal(boolean ifInject) {
			super.injectOnClickMethods(ifInject);
			return this;
		}
		
		@Override
		public InjectConfigure setToAll(boolean ifInject) {
			throw new UnsupportedOperationException("cannot invoke any 'set' methods");
		}
		
		@Override
		public InjectConfigure injectViews(boolean ifInject) {
			throw new UnsupportedOperationException("cannot invoke any 'set' methods");
		}
		
		@Override
		public InjectConfigure injectReses(boolean ifInject) {
			throw new UnsupportedOperationException("cannot invoke any 'set' methods");
		}
		
		@Override
		public InjectConfigure injectOnClickMethods(boolean ifInject) {
			throw new UnsupportedOperationException("cannot invoke any 'set' methods");
		}
	}
	
	/**
	 * 注入所有
	 */
	public static final InjectConfigure InjectAllConfigure;
	
	/**
	 * 只注入View的配置
	 */
	public static final InjectConfigure InjectViewConfigure;
	
	/**
	 * 只注入资源的配置
	 */
	public static final InjectConfigure InjectResConfigure;

	/**
	 * 只注入Click监听器的配置
	 */
	public static final InjectConfigure InjectOnClickMethodsConfigure;
	static {
		InjectAllConfigure = new SpecInjectConfigure()
			.setToAllInternal(true);
		
		
		InjectViewConfigure = new SpecInjectConfigure()
			.setToAllInternal(false).injectViewsInternal(true);
	
		
		InjectResConfigure = new SpecInjectConfigure()
			.setToAllInternal(false).injectResesInternal(true);
		
		
		InjectOnClickMethodsConfigure = new SpecInjectConfigure()
			.setToAllInternal(false).injectOnClickMethodsInternal(true);
	}
	
	// 设置客户端是否需要动态注入成员变量对应的Res资源
	private boolean mInjectReses = true;
	// 设置客户端是否需要动态注入成员变量对应的View
	private boolean mInjectViews = true;
	// 设置客户端是否需要动态注入onClick事件
	private boolean mInjectOnClickMethods = true;
	
	/**
	 * 创建一个新的配置对象。
	 */
	public InjectConfigure() {}
	
	/**
	 * 批量设置客户端是否需要动态注入
	 * @return 为了方便链式设置，返回原对象
	 */
	public InjectConfigure setToAll(boolean ifInject) {
		mInjectReses = ifInject;
		mInjectViews = ifInject;
		mInjectOnClickMethods = ifInject;
		return this;
	}
	
	/**
	 * 设置客户端是否需要动态注入成员变量对应的Res资源
	 * @return 为了方便链式设置，返回原对象
	 */
	public InjectConfigure injectReses(boolean ifInject) {
		mInjectReses = ifInject;
		return this;
	}

	/**
	 * 设置客户端是否需要动态注入成员变量对应的View
	 * @return 为了方便链式设置，返回原对象
	 */
	public InjectConfigure injectViews(boolean ifInject) {
		mInjectViews = ifInject;
		return this;
	}
	
	/**
	 * 设置客户端是否需要动态注入onClick事件
	 * @return 为了方便链式设置，返回原对象
	 */
	public InjectConfigure injectOnClickMethods(boolean ifInject) {
		mInjectOnClickMethods = ifInject;
		return this;
	}
	
	/**
	 * @return 是否设置客户端需要动态注入成员变量对应的Res资源
	 */
	public boolean isInjectReses() {
		return mInjectReses;
	}
	
	/**
	 * @return 是否设置客户端需要动态注入成员变量对应的View
	 */
	public boolean isInjectViews() {
		return mInjectViews;
	}
	
	/**
	 * @return 是否已经设置客户端动态注入onClick事件
	 */
	public boolean isInjectOnClickMethods() {
		return mInjectOnClickMethods;
	}

	@Override
	public String toString() {
		return "InjectConfigure [mInjectReses=" + mInjectReses
				+ ", mInjectViews=" + mInjectViews + ", mInjectOnClickMethods="
				+ mInjectOnClickMethods + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		
		if (this == o) {
			return true;
		}
		
		if (!(o instanceof InjectConfigure)) {
			return false;
		}
		
		InjectConfigure another = (InjectConfigure) o;
		
		return another.mInjectViews == mInjectViews
				&& 
				another.mInjectReses == mInjectReses
				&& 
				another.mInjectOnClickMethods == mInjectOnClickMethods;
	}
}
