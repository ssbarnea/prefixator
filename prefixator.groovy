#!/usr/bin/env groovy

def job_names = [
"jenkins-DFG-upgrades-bw-compat-mixed-versions-12-director-stage-upgrade-from-11-rhel-7.4-virt-composable_networker-ipv6-1234",
"jenkins-DFG-network-neutron-lbaas-12_director-rhel-virthost-3cont_2comp-ipv4-vxlan-ovs-secgroups-with-custom-guest-image-1234",
"jenkins-DFG-upgrades-upgrade-upgrade-11_director-rhel-virthost-3cont_3db_3msg_2net_2comp_3ceph-ipv6-vxlan-composable-workarounds-on-1234",
"phase2-9_director-rhel-7.3-virthost-3cont_2comp_3ceph-ipv4-gre-ceph-1234",
"nightly-10_packstack-rhel-7.4-openstack-all-in-one-neutron-rabbitmq",
"risk-rdo-pike-CentOS-7-virthost-1cont_1comp_1ceph-ipv4-vxlan-ceph",
"DFG-ui-python-openstackclient-7-unit-rhos",
"OSPD-Customized-Deployment-OVB-Cleanup",
"gate-infrared-tripleo-composable-roles",
"multijob-phase3-osp-6.0-RHEL-7-network",
"DFG-network-neutron-dsvm-fullstack-rhos",
]

def env = System.getenv()
// nv['GIT_COMMIT'] = "ce9a3c1404e8c91be604088670e93434c4253f03"
//Date date = new Date()
//String prefix = new Date().format("MMdd-HHmmss-") + "${env.BUILD_TAG ?: new Random().nextInt(10 ** 3)}"

def total_before = 0
def total_after = 0
def exceeding = 0
def job_cnt = 0
// 4 chars for '-XXX' hash suffix
// 12 chars for infrared suffixes like '-controller-0'
// 5 chars for allowing us to add 'DDHH-' time prefix
def MAX_SIZE = 63 - 4 - 14 - 5
println("INFO: Max prefix size ${MAX_SIZE} ...")

File fh = new File('jobs.txt')
File output = new File('jobs-after.txt')
output.write ""

fh.each { String j ->
    job_cnt += 1
    // env.BUILD_TAG
    j = j.toLowerCase().replaceAll('_','-')
    .replaceAll('all-in-one','aio')
    .replaceAll('infrared','ir')
    .replaceAll('composable','cp')
    .replaceAll('two-ports','2p')
    .replaceAll('containers','cnt')
    .replaceAll('ctlplane','cpl')
    .replaceAll('packstack','ps')
    .replaceAll('nightly','n')
    .replaceAll('multijob','mj')
    .replaceAll(/multi-?node/,'mn')
    .replaceAll('virthost','vh')
    .replaceAll('gate','g')
    .replaceAll('risk','r')
    .replaceAll(/upgrade[s]?/,'u')
    .replaceAll('phase','p')
    .replaceAll('fullstack','fs')
    .replaceAll('director','dir')
    .replaceAll('network','net')
    .replaceAll('neutron','neu')
    .replaceAll('storage','sto')
    .replaceAll('sahara','sad')
    .replaceAll('manila','man')
    .replaceAll('opendaylight','old')
    .replaceAll('image','i')
    .replaceAll('secgroups','sg')
    .replaceAll('openstack','os')
    .replaceAll('guest','g')
    .replaceAll('custom(ized)?','cst')
    .replaceAll('deploy(ment)?','dpl')
    .replaceAll('stage','st')
    .replaceAll('mixed-versions','mxv')
    .replaceAll('compat','cpt')
    .replaceAll('baremetal','bm')
    .replaceAll('bonding','bnd')
    .replaceAll('performance','perf')
    .replaceAll('workflow','wf')
    .replaceAll('rabbitmq','rmq')
    .replaceAll('monolithic','monolit')

    def l = j.size()
    def j2 = j.replaceAll( /(jenkins-|dfg-|-rhos|rhos-|-rhel-\d\.\d)|-7\.\d|-rhel|python-|-ipv[46]|-\d(cont|ceph|db|msg|net|comp)|virt|-(from|with|vxlan|vlan)/, '')
    def l2 = j2.size()

    total_before += l
    total_after += l2

    if (l2 > MAX_SIZE) {
      exceeding += 1
      println("WARN: ${l} -> ${l2} : ${j2}")
    }
    output << "${j2}\n"
}

println("INFO: ${total_after*100/total_before} - ${exceeding}/${job_cnt} over the limit")
