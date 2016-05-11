package a.b.c.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

public class EventManager {

	/**
	 * _fromClass：从哪个类发出事件
	 * _toClass：在哪个类接收事件
	 */
	@Produce
	public static Finish newFinish(@NonNull Class<?> _fromClass, @NonNull Class<?> _toClass) {
		return new Finish(_fromClass, _toClass);
	}

	public static class Finish {
		@NonNull
		public Class<?> _fromClass;
		@NonNull
		public Class<?> _toClass;

		public Finish(@NonNull Class<?> _fromClass, @NonNull Class<?> _toClass) {
			this._fromClass = _fromClass;
			this._toClass = _toClass;
		}
	}

	/**
	 * _fromClass：从哪个类发出事件
	 * _toClass：在哪个类接收事件
	 */
	@Produce
	public static Update newUpdate(@NonNull Class<?> _fromClass, @NonNull Class<?> _toClass) {
		return new Update(_fromClass, _toClass);
	}

	/**
	 * _fromClass：从哪个类发出事件
	 * _toClass：在哪个类接收事件
	 * _object：传递的数据，可以为任意
	 */
	@Produce
	public static Update newUpdate(@NonNull Class<?> _fromClass, @NonNull Class<?> _toClass, @Nullable Object _object) {
		return new Update(_fromClass, _toClass, _object);
	}

	public static class Update {

		@NonNull
		public Class<?> _fromClass;
		@NonNull
		public Class<?> _toClass;
		@Nullable
		public Object _object;

		public Update(@NonNull Class<?> _fromClass, @NonNull Class<?> _toClass) {
			this._fromClass = _fromClass;
			this._toClass = _toClass;
		}

		public Update(@NonNull Class<?> _fromClass, @NonNull Class<?> _toClass, @Nullable Object _object) {
			this._fromClass = _fromClass;
			this._toClass = _toClass;
			this._object = _object;
		}
	}

	@NonNull
	private static final Bus EVENT = new Bus();

	@NonNull
	public static Bus get() {
		return EVENT;
	}

	private EventManager() {
	}
}
