package org.bestbarcelona.votacions.datasource;

import java.util.List;

/**
 * Created by Pau on 3/8/15.
 */
public interface DataSourceCallBackLong {

    void handleDatasourceCallBack(List candidates, List votes, Number totalVotes, Boolean success);

}
