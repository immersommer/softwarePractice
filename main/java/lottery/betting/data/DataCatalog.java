/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lottery.betting.data;

import org.salespointframework.catalog.Catalog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Streamable;

public interface DataCatalog extends Catalog<Data> {

	@Override
	Streamable<Data> findAll();

	@Query("SELECT u FROM Data u WHERE u.category = ?1 AND u.set = false ORDER BY u.date")
	Streamable<Data> findByCategory(Category category);

	@Query("SELECT u FROM Data u WHERE u.productIdentifier.id = ?1 AND u.set = false")
	Data findById(String id);

	@Query("SELECT u FROM Data u WHERE u.set = true ORDER BY u.date DESC")
	Streamable<Data> findSet();

	@Query("DELETE FROM Data u WHERE u.productIdentifier.id = ?1")
	void deleteById(String id);
}
