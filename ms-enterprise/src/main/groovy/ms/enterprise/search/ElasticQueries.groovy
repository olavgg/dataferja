package ms.enterprise.search

import groovy.transform.CompileStatic

@CompileStatic
class ElasticQueries {

    public static String getMunicipalityQuery(String query){
        return """
{
  "query": {
    "match_phrase_prefix": {
      "search_label": {
        "query": "${query}",
        "slop": 10
      }
    }
  },
  "fields": [],
  "from": 0,
  "size": 20
}
"""
    }


    public static String getAttributesQuery(String query){
        return """
{
  "query": {
    "match_phrase_prefix": {
      "label": {
        "query": "${query}",
        "slop": 10
      }
    }
  },
  "fields": ["label"],
  "from": 0,
  "size": 20
}
"""
    }


    public static String getAttrQueryForMunicipalities(
            String query,
            Set<Long> municipalityIds
    ){

        return """
{
  "query": {
    "bool": {
      "must": [
        {
          "terms": {
            "municipality_id": [${municipalityIds.join(',')}]
          }
        },
        {
          "match_phrase_prefix": {
            "label": {
              "query": "${query}",
              "slop": "10"
            }
          }
        }
      ]
    }
  },
  "fields": [
    "label",
    "municipality_id"
  ],
  "from": 0,
  "size": 99
}
"""
    }
}
