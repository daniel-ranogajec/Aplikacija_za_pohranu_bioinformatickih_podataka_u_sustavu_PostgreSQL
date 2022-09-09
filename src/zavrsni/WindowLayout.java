package zavrsni;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.function.Function;

/**
 * Layout for OrganismWindow.
 * 
 * @author Daniel_Ranogajec
 *
 */
public class WindowLayout implements LayoutManager2{

	public static Integer FIRST_ELEMENT = 0;
	public static Integer SECOND_ELEMENT = 1;
	public static Integer THIRD_ELEMENT = 2;

	private Component[] components = new Component[3];


	@Override
	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();
		int x = insets.left;
		int y = insets.top;
		int height = (parent.getHeight() - insets.top - insets.bottom) / 3 - 10;
		int width= (parent.getWidth() - insets.left - insets.right) - 10;

		components[0].setBounds(y + 5, x + 5, width, (int)(1.2*height));
		components[1].setBounds(y + 5, x + (int)(1.2*height) + 15, width, (int)(0.6*height));
		components[2].setBounds(y + 5, x + (int)(1.8*height) + 25, width, (int)(1.2*height));
	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		if (comp == null || constraints == null)
			throw new NullPointerException();

		if (constraints instanceof Integer) 
			this.components[(int)constraints] = comp;
		else
			throw new IllegalArgumentException();
	}
	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return getLayoutSize(parent, Component::getMinimumSize);
	}
	
	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return getLayoutSize(parent, Component::getPreferredSize);
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return getLayoutSize(target, Component::getMaximumSize);
	}

	private Dimension getLayoutSize(Container parent, Function<Component, Dimension> func) {
		int maxWidth = 0;
		int maxHeight = 0;
		Insets insets = parent.getInsets();
		for (int i = 0; i < 3; i++) {
			if (components[i] != null) {
				Dimension d = func.apply(components[i]);
				if (d.height > maxHeight)
					maxHeight = d.height;
				if (d.width > maxWidth)
					maxWidth = d.width;
			}
		}
		return new Dimension(maxWidth + insets.left + insets.right
				, maxHeight * 3 + insets.top + insets.bottom);
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		throw new UnsupportedOperationException();		
	}



	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {	
		return;
	}

}
