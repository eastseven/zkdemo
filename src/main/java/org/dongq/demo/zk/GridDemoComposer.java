package org.dongq.demo.zk;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Grid;

public class GridDemoComposer extends GenericForwardComposer<Grid> {

	private static final long serialVersionUID = 1L;

	Grid gridDemo;
	
	@Override
	public void doAfterCompose(Grid comp) throws Exception {
		super.doAfterCompose(comp);
		
		System.out.println("GridDemoComposer.doAfterCompose.gridDemo: " + gridDemo);
		
		gridDemo.addEventListener("onPaging", new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				Grid g = (Grid) event.getTarget();
				int pageNo = g.getPaginal().getActivePage();
				System.out.println("onPaging event: pageNo=" + pageNo);
				
			}
		});
	}
}
