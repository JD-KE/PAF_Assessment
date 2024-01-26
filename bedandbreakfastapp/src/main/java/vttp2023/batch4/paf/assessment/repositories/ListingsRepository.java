package vttp2023.batch4.paf.assessment.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 * 
	 * db.listings.aggregate([
		{
			$match:{"address.suburb":{$exists:true, $ne:""}}
		},
		{
			$group:{
				_id:"$address.suburb"
			}
		},
		{
			$sort:{_id:1}
		}
		])
	 */
	public List<String> getSuburbs(String country) {
		MatchOperation matchOperation = Aggregation.match(Criteria.where("address.suburb").exists(true).ne(""));
		GroupOperation groupOperation = Aggregation.group("address.suburb");
		SortOperation sortOperation = Aggregation.sort(Sort.by(Direction.ASC,"_id"));
		Aggregation pipline = Aggregation.newAggregation(matchOperation,groupOperation,sortOperation);
		AggregationResults<Document> results = template.aggregate(pipline, "listings", Document.class);
		List<String> suburbs = new ArrayList<>();
		for (Document d:results){
			String suburb = d.getString("_id");
			suburbs.add(suburb);
		}
		// Query query = new Query(Criteria.where("address.suburb").exists(true).ne(""));
		
		return suburbs;
	}

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 *
	 *
	 * db.listings.find({
    "address.suburb":{$regex:"avalon",$options:"i"},
    price:{$lte:1000},
    accommodates:{$gte:1},
    min_nights:{$lte:1}
},
{
    name:1,
    accommodates:1,
    price:1
}
).sort({price:-1});
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		Query query = new Query(Criteria.where("address.suburb").regex(suburb,"i").and("price").lte(priceRange).and("accommodates").gte(persons).and("min_nights").lte(duration)).with(Sort.by(Direction.DESC,"price"));
		query.fields()
			.include("name", "accommodates", "price");

		List<AccommodationSummary> listSummaries = template.find(query, Document.class, "listings")
			.stream()
			.map(d ->{
				AccommodationSummary summary = new AccommodationSummary();
				summary.setId(d.getString("_id"));
				summary.setName(d.getString("name"));
				summary.setAccomodates(d.getInteger("accommodates"));
				summary.setPrice(d.get("price", Number.class).floatValue());
				return summary;
			})
			.toList();
		return listSummaries;
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
