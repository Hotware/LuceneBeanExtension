package de.hotware.lucene.extension.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.FieldType;

import de.hotware.lucene.extension.bean.BeanField.AnalyzerWrapper;
import de.hotware.lucene.extension.bean.BeanField.TypeWrapper;

/**
 * @author Martin Braun
 */
public class BeanInformationCacheImpl implements BeanInformationCache {

	public static final int DEFAULT_CACHE_SIZE = 1000;

	private final Map<Class<?>, List<FieldInformation>> annotatedFieldsCache;
	private final Lock annotatedFieldsCacheLock;
	private final Map<Class<?>, PerFieldAnalyzerWrapper> perFieldAnalyzerWrapperCache;
	private final Lock perFieldAnalyzerWrapperCacheLock;

	/**
	 * calls {@link #BeanInformationCacheImpl(int)} with
	 * {@value #DEFAULT_CACHE_SIZE} as the default parameter
	 * 
	 * @see #BeanConverterImpl(int)
	 */
	public BeanInformationCacheImpl() {
		this(DEFAULT_CACHE_SIZE);
	}

	public BeanInformationCacheImpl(int cacheSize) {
		this.annotatedFieldsCacheLock = new ReentrantLock();
		this.annotatedFieldsCache = new CacheMap<Class<?>, List<FieldInformation>>(cacheSize);
		this.perFieldAnalyzerWrapperCacheLock = new ReentrantLock();
		this.perFieldAnalyzerWrapperCache = new CacheMap<Class<?>, PerFieldAnalyzerWrapper>(cacheSize);
	}

	@Override
	public List<FieldInformation> getFieldInformations(Class<?> clazz) {
		this.annotatedFieldsCacheLock.lock();
		try {
			List<FieldInformation> fieldInformations = this.annotatedFieldsCache
					.get(clazz);
			if(fieldInformations == null) {
				fieldInformations = new ArrayList<FieldInformation>();
				Field[] fields = clazz.getDeclaredFields();
				for(Field field : fields) {
					if(field.isAnnotationPresent(BeanField.class)) {
						field.setAccessible(true);
						BeanField bf = field.getAnnotation(BeanField.class);
						TypeWrapper typeWrapper = bf.type();
						FieldType fieldType = new FieldType();
						fieldType.setIndexed(bf.index());
						fieldType.setStored(bf.store());
						if(typeWrapper.getNumericType() != null) {
							fieldType.setNumericType(bf.type().getNumericType());
						}
						fieldType.freeze();
						FieldInformation fieldInformation = new FieldInformation(field,
								field.getType(),
								fieldType,
								field.getAnnotation(BeanField.class));
						fieldInformations.add(fieldInformation);
					}
				}
				this.annotatedFieldsCache.put(clazz, fieldInformations);
			}
			return fieldInformations;
		} finally {
			this.annotatedFieldsCacheLock.unlock();
		}
	}

	@Override
	public PerFieldAnalyzerWrapper getPerFieldAnalyzerWrapper(Class<?> clazz,
			List<FieldInformation> fieldInformations,
			String locale) {
		this.perFieldAnalyzerWrapperCacheLock.lock();
		try {
			PerFieldAnalyzerWrapper wrapper = this.perFieldAnalyzerWrapperCache
					.get(clazz);
			if(wrapper == null) {
				Analyzer defaultAnalyzer = BeanField.DEFAULT_ANALYZER
						.getAnalyzer(locale);
				Map<String, Analyzer> fieldAnalyzers = new HashMap<String, Analyzer>();
				for(FieldInformation info : fieldInformations) {
					String fieldName = info.getField().getName();
					BeanField bf = info.getBeanField();
					AnalyzerWrapper analyzer = bf.analyzer();
					if(!analyzer.equals(defaultAnalyzer)) {
						fieldAnalyzers.put(fieldName,
								analyzer.getAnalyzer(locale));
					}
				}
				wrapper = new PerFieldAnalyzerWrapper(defaultAnalyzer,
						fieldAnalyzers);
				this.perFieldAnalyzerWrapperCache.put(clazz, wrapper);
			}
			return wrapper;
		} finally {
			this.perFieldAnalyzerWrapperCacheLock.unlock();
		}
	}

}