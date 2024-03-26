package sdcreator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.InflaterInputStream;

public class Main extends JFrame {

    private JTextField nameTextField;
    private JTextField displayNameTextField;
    private JTextField addLevelTextField;
    private JComboBox<String> itemComboBox;
    private JList<String> effectsList = new JList<>();
    private JComboBox<String> effectComboBox;
    private JTextField timeTextField;
    private JTextField intensityTextField;
    private JButton addButton;
    private JButton removeButton;
    private JButton generateButton;
    private JTextArea outputTextArea;
    private JComboBox<String>[] dropdownMenus;
    private JScrollPane effectsScrollPane = new JScrollPane(effectsList);

    public String[] mats;
    public String[] effectOptions;

    public ArrayList<Effect> effects = new ArrayList<>();
    public Effect lastEffect = null;

    public Main() {

        try {
            this.mats = load("/Material.lst.z");
            this.effectOptions = load("/PotionEffects.lst.z");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        DefaultListModel<String> model = new DefaultListModel<>();
        effectsList.setModel(model);
        setTitle("GUI Program");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(4, 2));
        JLabel nameLabel = new JLabel("Name:");
        nameTextField = new JTextField();
        JLabel displayNameLabel = new JLabel("Display Name:");
        displayNameTextField = new JTextField();
        JLabel addLevelLabel = new JLabel("Addiction Level:");
        addLevelTextField = new JTextField();
        JLabel itemLabel = new JLabel("Item:");
        itemComboBox = new JComboBox<>(mats);
        topPanel.add(nameLabel);
        topPanel.add(nameTextField);
        topPanel.add(displayNameLabel);
        topPanel.add(displayNameTextField);
        topPanel.add(addLevelLabel);
        topPanel.add(addLevelTextField);
        topPanel.add(itemLabel);
        topPanel.add(itemComboBox);

        JPanel centerPanel = new JPanel(new GridLayout(4, 3));
        dropdownMenus = new JComboBox[9];
        for (int i = 0; i < 9; i++) {
            dropdownMenus[i] = new JComboBox<>(mats);
            centerPanel.add(dropdownMenus[i]);
        }

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        bottomPanel.add(effectsScrollPane, gbc);

        gbc.gridy++;
        JPanel effectPanel = new JPanel(new GridLayout(1, 2));
        JLabel effectLabel = new JLabel("Effect:");
        effectComboBox = new JComboBox<>(effectOptions);
        effectPanel.add(effectLabel);
        effectPanel.add(effectComboBox);
        bottomPanel.add(effectPanel, gbc);

        gbc.gridy++;
        JPanel optionsPanel = new JPanel(new GridLayout(1, 4));
        JLabel timeLabel = new JLabel("Time:");
        timeTextField = new JTextField();
        JLabel intensityLabel = new JLabel("Intensity:");
        intensityTextField = new JTextField();
        optionsPanel.add(timeLabel);
        optionsPanel.add(timeTextField);
        optionsPanel.add(intensityLabel);
        optionsPanel.add(intensityTextField);
        bottomPanel.add(optionsPanel, gbc);

        gbc.gridy++;
        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        bottomPanel.add(addButton, gbc);
        gbc.gridx++;
        bottomPanel.add(removeButton, gbc);

        JPanel outputPanel = new JPanel(new BorderLayout());
        generateButton = new JButton("Generate");
        outputTextArea = new JTextArea();
        outputPanel.add(generateButton, BorderLayout.NORTH);
        outputPanel.add(outputTextArea, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(outputPanel, BorderLayout.EAST);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add button event listener logic
                String effect = (String) effectComboBox.getSelectedItem();
                String time = timeTextField.getText();
                String intensity = intensityTextField.getText();
                // Perform desired actions
                int t = 0, i = 0;
                boolean witch = false;
                try {
                    t = Integer.parseInt(time);
                    witch = true;
                    i = Integer.parseInt(intensity);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Main.this, "Invalid input. " + (witch ? "intensity" : "Time") + " must a number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                lastEffect = new Effect(effect, i, t);
                effects.add(lastEffect);
                DefaultListModel<String> model = (DefaultListModel<String>) effectsList.getModel();
                model.addElement(String.format("%s %s for: %s %s", effect, intensity, time, (t == 1) ? "second" : "seconds"));
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Remove button event listener logic
                // Perform desired actions
                if (!effects.isEmpty()) {
                    effects.remove(lastEffect);
                    DefaultListModel<String> model = (DefaultListModel<String>) effectsList.getModel();
                    model.removeElementAt(model.getSize() - 1);
                    if (!effects.isEmpty()) {
                        lastEffect = effects.get(effects.size() - 1);
                    } else {
                        lastEffect = null;
                    }
                }
            }
        });

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Generate button event listener logic
                // Perform desired actions
                float addlevel;
                try {
                    addlevel = Float.parseFloat(addLevelTextField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Main.this, "Invalid input. Addiction Level must a number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                StringBuilder sb = new StringBuilder("  {\n    \"name\": \"");
                sb.append(nameTextField.getText());
                sb.append("\",\n    \"displayname\": \"");
                sb.append(displayNameTextField.getText());
                sb.append("\",\n");
                String[] r = new String[9];
                for (int i = 0; i < 9; i++) {
                    r[i] = mats[dropdownMenus[i].getSelectedIndex()];
                }
                sb.append(new Recipe(r).build());
                sb.append("    \"effects\": [\n");
                for (int i = 0; i < effects.size(); i++) {
                    sb.append(effects.get(i).build(i, effects.size()));
                }
                sb.append("    ],\n    \"item\": \"");
                sb.append(itemComboBox.getSelectedItem());
                sb.append("\",\n    \"addictionLevel\": ");
                sb.append(addlevel);
                sb.append("\n  },");
                outputTextArea.setText(sb.toString());
            }
        });
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            Main program = new Main();
            program.setVisible(true);
        });
    }

    //load compressed string arrays
    public static String[] load(String fn) throws IOException {
        InputStream str = Main.class.getResourceAsStream(fn);
        byte[] compressed = new byte[str.available()];
        str.read(compressed);
        InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(compressed));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[5];
        int rlen;
        while ((rlen = iis.read(buf)) != -1) {
            baos.write(Arrays.copyOf(buf, rlen));
        }
        return new String(baos.toByteArray()).split("\n");
    }
}

class Effect {

    String type;
    int intensity;
    int time;

    public Effect(String type, int intensity, int time) {
        this.type = type.trim();
        this.intensity = intensity;
        this.time = time;
    }

    public String build(int pos, int tot) {
        if (pos == tot - 1) {
            return String.format("      {\n"
                    + "        \"type\": \"%s\",\n"
                    + "        \"time\": %d,\n"
                    + "        \"intensity\": %d\n"
                    + "      }\n", type, time, intensity);
        } else {
            return String.format("      {\n"
                    + "        \"type\": \"%s\",\n"
                    + "        \"time\": %d,\n"
                    + "        \"intensity\": %d\n"
                    + "      },\n", type, time, intensity);
        }
    }
}

class Recipe {

    String[] grid;

    public Recipe(String[] grid) {
        this.grid = grid;
    }

    public String build() {
        StringBuilder sb = new StringBuilder("    \"recipe\": [\n");
        for (int i = 0; i < grid.length; i++) {
            sb.append("      \"");
            sb.append(grid[i]);
            if (i == grid.length - 1) {
                sb.append("\"\n");
            } else {
                sb.append("\",\n");
            }
        }
        sb.append("    ],\n");
        return sb.toString();
    }
}
