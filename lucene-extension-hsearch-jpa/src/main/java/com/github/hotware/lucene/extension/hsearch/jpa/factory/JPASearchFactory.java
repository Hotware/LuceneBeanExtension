package com.github.hotware.lucene.extension.hsearch.jpa.factory;

import java.io.Closeable;

import org.apache.lucene.search.Query;
import org.hibernate.search.backend.spi.WorkType;
import org.hibernate.search.indexes.IndexReaderAccessor;
import org.hibernate.search.query.dsl.QueryContextBuilder;
import org.hibernate.search.stat.Statistics;

import com.github.hotware.lucene.extension.hsearch.transaction.TransactionContext;
import com.github.hotware.lucene.extension.hsearch.jpa.event.EventConsumer;
import com.github.hotware.lucene.extension.hsearch.jpa.query.JPAHSearchQuery;

public interface JPASearchFactory extends Closeable, EventConsumer {
	
	public IndexReaderAccessor getIndexReaderAccessor();
	
	public QueryContextBuilder buildQueryBuilder();
	
	public void optimize();
	
	public void optimize(Class<?> entity);
	
	public Statistics getStatistics();
	
	public void doIndexWork(Iterable<Object> objects, WorkType workType, TransactionContext tc);

	public void doIndexWork(Iterable<Object> objects, WorkType workType);
	
	public void doIndexwork(Object object, WorkType workType);
	
	public void purgeAll(Class<?> entityClass);
	
	public <T> JPAHSearchQuery<T> createQuery(Query query, Class<T> targetedEntity);

}
