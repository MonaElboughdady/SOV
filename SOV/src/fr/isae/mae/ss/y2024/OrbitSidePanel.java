package fr.isae.mae.ss.y2024;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class OrbitSidePanel extends JPanel {
	
    private final CustomSlider sliderA;
    private final CustomSlider sliderE;
    private final CustomSlider sliderI;
    private final CustomSlider sliderOmega;
    private final CustomSlider sliderUpperOmega;
    private final CustomSlider sliderV;
    
    private final double defaultA = 2.0;
	private final double defaultE = 0.01;
    private final double defaultI = 0.2;
    private final double defaultOmega = 90.0;
    private final double defaultUpperOmega = 0.0;
    private final double defaultV = 0.0;
	
	public OrbitSidePanel() {
        // Configure panel layout and appearance
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(50, 50, 50, 200)); // Semi-transparent dark gray
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Padding
        
        // Initialize sliders
        sliderA = new CustomSlider(1.0, 6.0, defaultA, 10, "a");
        sliderE = new CustomSlider(0.0, 1.0, defaultE, 100, "e");
        sliderI = new CustomSlider(0.0, 180.0, defaultI, 1, "i");
        sliderOmega = new CustomSlider(0.0, 360.0, defaultOmega, 1, "ω");
        sliderUpperOmega = new CustomSlider(0.0, 360.0, defaultUpperOmega, 1, "Ω");
        sliderV = new CustomSlider(0.0, 360.0, defaultV, 1, "v");
        
        // Add sliders and spacers to the panel
        this.add(sliderA);
        this.add(Box.createVerticalStrut(10));
        this.add(sliderE);
        this.add(Box.createVerticalStrut(10));
        this.add(sliderI);
        this.add(Box.createVerticalStrut(10));
        this.add(sliderOmega);
        this.add(Box.createVerticalStrut(10));
        this.add(sliderUpperOmega);
        this.add(Box.createVerticalStrut(10));
        this.add(sliderV);
	}
	
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded background
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Rounded corners with radius 20
    }
    

    // Getters for slider values
    public double getSliderAValue() {
        return sliderA.getValue();
    }

    public double getSliderEValue() {
        return sliderE.getValue();
    }

    public double getSliderIValue() {
        return sliderI.getValue();
    }

    public double getSliderOmegaValue() {
        return sliderOmega.getValue();
    }

    public double getSliderUpperOmegaValue() {
        return sliderUpperOmega.getValue();
    }

    public double getSliderVValue() {
        return sliderV.getValue();
    }
    
    public double getDefaultA() {
		return defaultA;
	}

	public double getDefaultE() {
		return defaultE;
	}

	public double getDefaultI() {
		return defaultI;
	}

	public double getDefaultOmega() {
		return defaultOmega;
	}

	public double getDefaultUpperOmega() {
		return defaultUpperOmega;
	}

	public double getDefaultV() {
		return defaultV;
	}

    // Handlers to attach listeners
    public void addSliderAListener(CustomSlider.SliderValueChangeListener listener) {
        sliderA.addSliderValueChangeListener(listener);
    }

    public void addSliderEListener(CustomSlider.SliderValueChangeListener listener) {
        sliderE.addSliderValueChangeListener(listener);
    }

    public void addSliderIListener(CustomSlider.SliderValueChangeListener listener) {
        sliderI.addSliderValueChangeListener(listener);
    }

    public void addSliderOmegaListener(CustomSlider.SliderValueChangeListener listener) {
        sliderOmega.addSliderValueChangeListener(listener);
    }

    public void addSliderUpperOmegaListener(CustomSlider.SliderValueChangeListener listener) {
        sliderUpperOmega.addSliderValueChangeListener(listener);
    }

    public void addSliderVListener(CustomSlider.SliderValueChangeListener listener) {
        sliderV.addSliderValueChangeListener(listener);
    }

}
