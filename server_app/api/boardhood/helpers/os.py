import subprocess as SP


def children_pid(pid):
    """get the list of the children pids of a pid (linux only)"""
    proc = SP.Popen('ps -o pid,ppid ax | grep "%d"' % pid, shell=True, stdout=SP.PIPE)
    pidppid  = [x.split() for x in proc.communicate()[0].split("\n") if x]
    return list(int(p) for p, pp in pidppid if int(pp) == pid)
