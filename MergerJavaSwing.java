import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

class FileBuddy extends JFrame {

    private JTextArea statusTextArea;
    private JButton attachButton;
    private JButton mergeButton;
    private JButton commonButton;
    private JButton differenceButton;
    private JButton exitButton;
    private JFileChooser fileChooser;
    private File[] selectedFiles = new File[2];
    private int fileCount = 0;

    public FileBuddy() {
        setTitle("FileBuddy");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize components
        statusTextArea = new JTextArea(5, 40);
        statusTextArea.setEditable(false);

        attachButton = new JButton("Attach Files");
        mergeButton = new JButton("Merge Files");
        commonButton = new JButton("Common Elements");
        differenceButton = new JButton("Difference");
        exitButton = new JButton("Exit");
        mergeButton.setEnabled(false);
        commonButton.setEnabled(false);
        differenceButton.setEnabled(false);

        /* File chooser setup
        /Reffered from Geeksforgeeks
        URL: https://www.geeksforgeeks.org/java-swing-jfilechooser/
         */
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));

        // Add action listeners
        attachButton.addActionListener(new AttachFilesAction());
        mergeButton.addActionListener(new MergeFilesAction());
        commonButton.addActionListener(new CommonFilesAction());
        differenceButton.addActionListener(new DifferenceFilesAction());
        exitButton.addActionListener(e -> System.exit(0));

        // Layout
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(statusTextArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(attachButton);
        buttonPanel.add(mergeButton);
        buttonPanel.add(commonButton);
        buttonPanel.add(differenceButton);
        buttonPanel.add(exitButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Drop area
        DropArea dropArea = new DropArea();
        panel.add(dropArea, BorderLayout.NORTH);
    }

    private class DropArea extends JPanel {
        public DropArea() {
            setPreferredSize(new Dimension(650, 150));
            setBackground(new Color(0, 0, 0, 0)); // Transparent background
            setDropTarget(new DropTarget() {
                public synchronized void drop(DropTargetDropEvent evt) {
                    try {
                        /*
                        * Referred from Oracle java docs
                        * URL: https://docs.oracle.com/javase/8/docs/api/index.html?java/awt/dnd/DnDConstants.html
                        * */
                        evt.acceptDrop(DnDConstants.ACTION_COPY);

                        /*
                        * URL: https://docs.oracle.com/javase/8/docs/api/java/awt/datatransfer/DataFlavor.html
                        * */
                        List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        for (File file : droppedFiles) {
                            if (fileCount < 2 && file.getName().endsWith(".txt")) {
                                selectedFiles[fileCount++] = file;
                                statusTextArea.append("\nAttached: " + file.getName());
                            }
                        }
                        if (fileCount == 2) {
                            mergeButton.setEnabled(true);
                            commonButton.setEnabled(true);
                            differenceButton.setEnabled(true);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            /*
            * Referred form coderanch
            * URL: https://coderanch.com/t/656174/java/paintcomponent-method-draw-jframe
            * */
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(200, 200, 200, 100)); // Light gray with transparency
            g2d.fillRoundRect(200, 20, 250, 100, 20, 20); // Rounded rectangle

            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(320, 50, 320, 90); // Vertical line for plus
            g2d.drawLine(300, 70, 340, 70); // Horizontal line for plus

            g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2d.drawString("Drop or attach files here", 230, 140);
        }
    }

    private class AttachFilesAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            /*
            * Referred from Geeksforgeeks
            * URL: https://www.geeksforgeeks.org/java-swing-jfilechooser/
            * */
            int returnV = fileChooser.showOpenDialog(FileBuddy.this);
            if (returnV == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                if (files.length + fileCount > 2) {
                    JOptionPane.showMessageDialog(FileBuddy.this, "Maximum of 2 files allowed.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    for (File file : files) {
                        if (fileCount < 2) {
                            selectedFiles[fileCount++] = file;
                            statusTextArea.append("\nAttached: " + file.getName());
                        }
                    }
                    if (fileCount == 2) {
                        mergeButton.setEnabled(true);
                        commonButton.setEnabled(true);
                        differenceButton.setEnabled(true);
                    }
                }
            }
        }
    }

    //added all the necessary actionlisteners for different tasks
    private class MergeFilesAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            processFiles(Merge::merge, "merged_output.txt");
        }
    }

    private class CommonFilesAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            processFiles(Merge::common, "common_output.txt");
        }
    }

    private class DifferenceFilesAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            processFiles(Merge::difference, "difference_output.txt");
        }
    }

    private void processFiles(FileProcessor processor, String defaultFileName) {
        try {
            OrderedList<String> list1 = readFile(selectedFiles[0]);
            OrderedList<String> list2 = readFile(selectedFiles[1]);
            OrderedList<String> resultList = processor.process(list1, list2);

            JFileChooser saveFileChooser = new JFileChooser();
            saveFileChooser.setDialogTitle("Save Result File");
            saveFileChooser.setSelectedFile(new File(defaultFileName));
            int userSelection = saveFileChooser.showSaveDialog(FileBuddy.this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File resultFile = saveFileChooser.getSelectedFile();
                try (PrintWriter writer = new PrintWriter(resultFile)) {
                    for (int i = 0; i < resultList.size(); i++) {
                        writer.println(resultList.get(i));
                    }
                }
                JOptionPane.showMessageDialog(FileBuddy.this, "Operation completed successfully. Saved to: " + resultFile.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(FileBuddy.this, "Error processing files: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private OrderedList<String> readFile(File file) throws IOException {
        OrderedList<String> list = new OrderedList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (list.binarySearch(line) < 0) {
                    list.insert(line);
                }
            }
        }
        return list;
    }

    //this is the main class
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileBuddy().setVisible(true));
    }

    @FunctionalInterface
    interface FileProcessor {
        <T extends Comparable<T>> OrderedList<T> process(OrderedList<T> list1, OrderedList<T> list2);
    }
}