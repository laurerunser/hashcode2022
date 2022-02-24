import java.util.ArrayList;
import java.util.Comparator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    static int nb_contrib;
    static int nb_projects;
    static int max_time; // biggest best before * 2

    static ArrayList<Project> projects = new ArrayList<>();
    static ArrayList<Contributor> contributors = new ArrayList<>();

    static String[] filenames = {"a_an_example.in.txt",
            "b_better_start_small.in.txt", "c_collaboration.in.txt",
            "d_dense_schedule.in.txt", "e_exceptional_skills.in.txt",
            "f_find_great_mentors.in.txt"};

    public static void main(String[] args) throws FileNotFoundException {
        InputStream ins = new FileInputStream("src/input_data/" + filenames[2]);
        Scanner obj = new Scanner(ins);
        parse(obj);
        max_time = 40;
        LinkedList<Project> finis = new LinkedList<Project>();
        traitement(max_time, finis);
        /*for(int i=0;i<projects.size();i++){
            System.out.println(projects.get(i).roles);
        }*/
        try {
            write_sol("sol3.txt", finis);
        } catch (Exception E) {
            E.printStackTrace();
        }


    }


    // parser
    public static void parse(Scanner sc) {
        String[] l = sc.nextLine().split(" ");
        nb_contrib = Integer.parseInt(l[0]);
        nb_projects = Integer.parseInt(l[1]);

        // parse contributors
        for (int i = 0; i < nb_contrib; i++) {
            Contributor c = new Contributor();
            contributors.add(c);

            String[] l1 = sc.nextLine().split(" ");
            c.name = l1[0];
            c.nb_skill = Integer.parseInt(l1[1]);
            c.skills = new HashMap<>();
            for (int j = 0; j < c.nb_skill; j++) {
                String[] l2 = sc.nextLine().split(" ");
                c.skills.put(l2[0], Integer.parseInt(l2[1]));
            }
        }

        // parse projects
        for (int i = 0; i < nb_projects; i++) {
            Project p = new Project();
            projects.add(p);

            String[] l1 = sc.nextLine().split(" ");
            p.name = l1[0];
            p.time = Integer.parseInt(l1[1]);
            p.score = Integer.parseInt(l1[2]);
            p.best_before = Integer.parseInt(l1[3]);
            p.nb_contrib = Integer.parseInt(l1[4]);
            p.roles = new LinkedList<>();
            for (int j = 0; j < p.nb_contrib; j++) {
                Role r = new Role();
                p.roles.add(r);

                String[] l2 = sc.nextLine().split(" ");
                r.name = l2[0];
                r.skill = Integer.parseInt(l2[1]);
            }
        }
    }

    // algo

    static void sort_projects() {
        projects.sort((o1, o2) -> {
            if (o1.score == o2.score) {
                return 0;
            } else if (o1.score > o2.score) {
                return 1;
            } else {
                return -1;
            }
        });
    }

    static ArrayList<Contributor> get_available_contributors(int current_time) {
        ArrayList<Contributor> available = new ArrayList<>();
        for (Contributor c : contributors) {
            if (c.available <= current_time) {
                available.add(c);
            }
        }
        return available;
    }

    static void traitement(int max_time, LinkedList<Project> finis) {
        sort_projects();

        for (int current_time = 0; current_time < max_time; current_time++) {
            System.out.println(current_time);
            System.out.println(get_available_contributors(current_time));
            for (int i = 0; i < projects.size(); i++) {
                Project p = projects.get(i);
                if (!p.completed) {
                    LinkedList<Role> ptmp = p.roles;
                    Role last = ptmp.getLast();
                    Role rtmp;
                    ArrayList<Contributor> available = get_available_contributors(current_time);
                    boolean f = true;
                    Contributor[] used = new Contributor[p.roles.size()];
                    LinkedList<String> usedskill = new LinkedList<String>();
                    HashMap<String, Integer> usedskillval = new HashMap<String, Integer>();
                    for(int roleIndex = 0; roleIndex<p.roles.size(); roleIndex++) {
                        rtmp = ptmp.get(roleIndex);
                        used = skill_available(available, rtmp.name, roleIndex, rtmp.skill, used);
                        if (used[roleIndex] == null) {
                            f = false; // project can't be filled
                            break;
                        } else { // add contributor
                            rtmp.contributor = used[roleIndex].name;
                            usedskill.add(rtmp.name);
                            usedskillval.put(rtmp.name, rtmp.skill);
                        }
                    }
                    if (f) { // if the project if filled
                        for (int k = 0; k < used.length; k++) {
                            used[k].available = current_time + p.time;
                            String skill = usedskill.get(k);
                            if (used[k].skills.get(skill) <= usedskillval.get(skill)) {
                                used[k].skills.put(skill, used[k].skills.get(skill) + 1);
                            }
                            if (!(finis.contains(p))) {
                                finis.add(p);
                            }
                            System.out.println(p.name);
                            p.completed = true;
                        }
                    }
                }
            }
        }

    }

    public static Contributor[] skill_available(ArrayList<Contributor> a, String s, int skillIndex, int lvl, Contributor[] used) {
        for (int i = 0; i < a.size(); i++) {
            Contributor tmp = a.get(i);
            if (tmp.skills.containsKey(s)) {
                int clvl = tmp.skills.get(s);
                if (clvl >= lvl) {
                    a.remove(tmp);
                    used[skillIndex] = tmp;
                    return used;
                }
            }
        }
        return used;
    }

    static public void write_sol(String filePath, ArrayList<Project> ps) throws IOException {
        PrintWriter writer = new PrintWriter(filePath, StandardCharsets.US_ASCII);
        LinkedList<Project> completed = new LinkedList<Project>();
        int i = 0;
        for (int j = 0; j < ps.size(); j++) {
            System.out.println(ps.get(j).completed);
            if (ps.get(j).completed) {
                completed.add(ps.get(j));
                i++;
            }
        }
        writer.println(Integer.toString(i));
        for (int j = 0; j < i; j++) {
            LinkedList<Role> tmp = completed.get(j).roles;
            writer.println(completed.get(j).name);
            String ret = tmp.get(0).contributor;
            for (int k = 1; k < tmp.size(); k++) {
                ret += " " + tmp.get(k).contributor;
            }
            writer.println(ret);
        }
        writer.close();
    }

    static public void write_sol(String filePath, LinkedList<Project> ps) throws IOException {
        PrintWriter writer = new PrintWriter(filePath, StandardCharsets.US_ASCII);
        writer.println(Integer.toString(ps.size()));
        for (int j = 0; j < ps.size(); j++) {
            LinkedList<Role> tmp = ps.get(j).roles;
            writer.println(ps.get(j).name);
            String ret = tmp.get(0).contributor;
            for (int k = 1; k < tmp.size(); k++) {
                ret += " " + tmp.get(k).contributor;
            }
            writer.println(ret);
        }
        writer.close();
    }


    // write sol
}
