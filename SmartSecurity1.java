package my.project;

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class SmartSecurity1 extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton service1Button;
    private JButton service2Button;
    private JButton service3Button;

    public SmartSecurity1() {
        setTitle("gRPC Client GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);

        // Crear componentes
        service1Button = new JButton("Service 1");
        service2Button = new JButton("Service 2");
        service3Button = new JButton("Service 3");

        // Agregar componentes al panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
        panel.add(service1Button);
        panel.add(service2Button);
        panel.add(service3Button);

        // Agregar el panel al frame
        add(panel);

        // Agregar listeners a los botones
        service1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para invocar el Service 1 aquí
                // Por ejemplo, crea una instancia de tu cliente gRPC y llama a tu función Service 1
                JOptionPane.showMessageDialog(SmartSecurity1.this, "Service 1 clicked!");
            }
        });

        service2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para invocar el Service 2 aquí
                // Por ejemplo, crea una instancia de tu cliente gRPC y llama a tu función Service 2
                JOptionPane.showMessageDialog(SmartSecurity1.this, "Service 2 clicked!");
            }
        });

        service3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para invocar el Service 3 aquí
                // Por ejemplo, crea una instancia de tu cliente gRPC y llama a tu función Service 3
                JOptionPane.showMessageDialog(SmartSecurity1.this, "Service 3 clicked!");
            }
        });

        // Mostrar la GUI
        setLocationRelativeTo(null); // Centrar en la pantalla
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SmartSecurity1();
            }
        });
    }
}

/*public class SmartSecurity1 extends JFrame {

	private JPanel contentPane;


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SmartSecurity1 frame = new SmartSecurity1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public SmartSecurity1() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
	}

}*/
