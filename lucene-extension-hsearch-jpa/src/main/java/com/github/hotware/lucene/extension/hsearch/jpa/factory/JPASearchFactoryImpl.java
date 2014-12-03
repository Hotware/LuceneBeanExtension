package com.github.hotware.lucene.extension.hsearch.jpa.factory;

import java.io.IOException;

import org.apache.lucene.search.Query;
import org.hibernate.search.backend.spi.WorkType;
import org.hibernate.search.indexes.IndexReaderAccessor;
import org.hibernate.search.query.dsl.QueryContextBuilder;
import org.hibernate.search.stat.Statistics;

import com.github.hotware.lucene.extension.hsearch.factory.SearchFactory;
import com.github.hotware.lucene.extension.hsearch.transaction.TransactionContext;
import com.github.hotware.lucene.extension.hsearch.jpa.query.JPAHSearchQuery;
import com.github.hotware.lucene.extension.hsearch.jpa.query.JPAHSearchQueryImpl;

public class JPASearchFactoryImpl implements JPASearchFactory {

	private final SearchFactory searchFactory;

	public JPASearchFactoryImpl(SearchFactory searchFactory) {
		this.searchFactory = searchFactory;
	}

	@Override
	public void index(Iterable<Object> entities, TransactionContext tc) {
		this.doIndexWork(entities, WorkType.ADD, tc);
	}

	@Override
	public void update(Iterable<Object> entities, TransactionContext tc) {
		this.doIndexWork(entities, WorkType.UPDATE, tc);
	}

	@Override
	public void delete(Iterable<Object> entities, TransactionContext tc) {
		this.doIndexWork(entities, WorkType.PURGE, tc);
	}

	@Override
	public IndexReaderAccessor getIndexReaderAccessor() {
		return this.searchFactory.getIndexReaderAccessor();
	}

	@Override
	public void close() throws IOException {
		this.searchFactory.close();
	}

	@Override
	public QueryContextBuilder buildQueryBuilder() {
		return this.searchFactory.buildQueryBuilder();
	}

	@Override
	public void optimize() {
		this.searchFactory.optimize();
	}

	@Override
	public void optimize(Class<?> entity) {
		this.searchFactory.optimize(entity);
	}

	@Override
	public Statistics getStatistics() {
		return this.searchFactory.getStatistics();
	}

	@Override
	public void doIndexWork(Iterable<Object> objects, WorkType workType) {
		this.searchFactory.doIndexWork(objects, workType);
	}

	@Override
	public void doIndexwork(Object object, WorkType workType) {
		this.searchFactory.doIndexwork(object, workType);
	}
	
	@Override
	public void doIndexWork(Iterable<Object> objects, WorkType workType,
			TransactionContext tc) {
		this.searchFactory.doIndexWork(objects, workType, tc);
	}

	@Override
	public <T> JPAHSearchQuery<T> createQuery(Query query,
			Class<T> targetedEntity) {
		return new JPAHSearchQueryImpl<T>(this.searchFactory.createQuery(query,
				targetedEntity));
	}

	@Override
	public void purgeAll(Class<?> entityClass) {
		this.searchFactory.purgeAll(entityClass);
	}

}
