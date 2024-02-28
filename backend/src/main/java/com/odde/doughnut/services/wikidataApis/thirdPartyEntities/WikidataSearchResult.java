package com.odde.doughnut.services.wikidataApis.thirdPartyEntities;

import com.odde.doughnut.controllers.dto.WikidataSearchEntity;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WikidataSearchResult {
  public List<Map<String, Object>> search;

  public List<WikidataSearchEntity> getWikidataSearchEntities() {
    return search.stream().map(WikidataSearchEntity::new).collect(Collectors.toList());
  }
}
