package cn.ac.iscas.cloudeploy.v2.model.service.topology;

import java.io.IOException;

import com.google.common.base.Optional;

import cn.ac.iscas.cloudeploy.v2.dataview.application.ApplicationView.DetailedItem;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.NodeTemplate;
import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;

public interface STModelService {
	public Optional<String> transViewToSTM(DetailedItem view) throws IOException;
	public ServiceTemplate findServiceTemplateOfApplication(Application app);
	public Optional<NodeTemplate> findNodeTemplateOfContainer(ServiceTemplate stm, Container container);
}
