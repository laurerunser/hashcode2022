import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
        InputStream ins = new FileInputStream("src/input_data/" + filenames[0]);
        Scanner obj = new Scanner(ins);
        parse(obj);
        max_time = 1000; // change to biggest *2
        traitement(max_time, projects);

        write_sol();
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
            if (c.available >= current_time) {
                available.add(c);
            }
        }
        return available;
    }

    static void traitement(int max_time, ArrayList<Project> projects) {
        sort_projects();

        // go day by day
        for (int current_time = 0; current_time < max_time; current_time++) {
            // for each project, start with the biggest score
            for (int i = 0; i < nb_projects; i++) {
                ArrayList<Contributor> available_contributors = get_available_contributors(current_time);
                Project p_current = projects.get(i);
                if (p_current.completed) continue;

                ArrayList<Contributor> candidates = new ArrayList<>();

                for (Role r : p_current.roles) {
                    if (r.contributor.equals("")) continue;

                    // try to find a mentee
                    Optional<Contributor> mentee = available_contributors.stream()
                            .filter(c -> c.skills.get(r.name) == r.skill - 1)
                            .findFirst();

                    // try to find a mentor
                    if (mentee.isPresent()) {
                        boolean found_mentor = find_mentor(mentee.get(), available_contributors, r, p_current, candidates);
                        if (!found_mentor) {
                            // can't find a mentor, need to find a normal person to fill the role
                            Optional<Contributor> person = available_contributors.stream()
                                    .filter(c -> c.skills.get(r.name) >= r.skill)
                                    .findFirst();
                            if (person.isPresent()) {
                                // remove from available & add to the role
                                available_contributors.remove(person.get());
                                r.name = person.get().name;
                                candidates.add(person.get());
                            }
                        }
                    }
                }
                if (all_roles_filled(p_current)) {
                    update_time_contributors(candidates, current_time, p_current);
                    p_current.completed = true;
                } else {
                    remove_all_contribs(p_current);
                }
            }
        }
    }

    static boolean all_roles_filled(Project p) {
        for (Role r : p.roles) {
            if (r.name.equals("")) {
                return false;
            }
        }
        return true;
    }

    static void remove_all_contribs(Project p) {
        for (Role r : p.roles) {
            r.name = "";
        }
    }

    static void update_time_contributors(ArrayList<Contributor> candidates, int current_time, Project p) {
        candidates.forEach(c -> c.available = current_time + p.time);
    }

    static boolean find_mentor(Contributor mentee, ArrayList<Contributor> available_contributors, Role r,
                               Project p_current, ArrayList<Contributor> candidates) {
        for (Contributor mentor : available_contributors) {
            if (mentor == mentee) continue;
            // mentor has the skill
            if (mentor.skills.get(r.name) >= r.skill) {
                // try to find them a role
                for (Role r2 : p_current.roles) {
                    if (r2.contributor.equals("") // role empty
                            && mentor.skills.get(r2.name) >= r2.skill // right skill
                    ) {
                        // ok found a mentor : add both to the list
                        r.contributor = mentee.name;
                        r2.contributor = mentor.name;

                        // remove them from available
                        available_contributors.remove(mentee);
                        available_contributors.remove(mentor);

                        // add them to the candidates
                        candidates.add(mentee);
                        candidates.add(mentor);
                        return true;
                    }
                }
            }
        }
        return false;
    }


    // write sol

    static void write_sol() {

    }
}
