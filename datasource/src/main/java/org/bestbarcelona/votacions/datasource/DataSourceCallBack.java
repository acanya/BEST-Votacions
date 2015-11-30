package org.bestbarcelona.votacions.datasource;

import java.util.List;

/**
 * Created by Pau on 17/5/15.
 */
public interface DataSourceCallBack
{
    void handleDatasourceCallBack(List objects, Boolean success);

}

